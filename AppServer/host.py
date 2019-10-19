# -*- coding: utf-8 -*-
# host.py
# Copyright (C) 2019 KunoiSayami
#
# This module is part of 1081-NiceDemo and is released under
# the AGPL v3 License: https://www.gnu.org/licenses/agpl-3.0.txt
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see <https://www.gnu.org/licenses/>.
import datetime
import random
import hashlib
import os
import time
from libpy3.mysqldb import mysqldb
from configparser import ConfigParser
from http.server import HTTPServer
from server import Server as _exServer
from http_status_code import HTTP_STATUS_CODES
import fcmbackend
import json

expire_day = 2 * 60 * 60 * 24

class Server(_exServer):
	def log_login_attmept(self, user: str, b: bool):
		Server.conn.execute("INSERT INTO `log_login` (`attempt_user`, `success`) VALUE (%s, %s)", (user, 'Y' if b else 'N'))
		if b:
			Server.conn.execute("UPDATE `accounts` SET `last_login` = CURRENT_TIMESTAMP() WHERE `username` = %s", user)

	def verify_user_session(self, A_auth: str):
		if A_auth is None:
			return False, HTTP_STATUS_CODES.ERROR_USER_SESSION_MISSING, None
		sqlObj = Server.conn.query1("SELECT `user_id`, `timestamp` FROM `user_session` WHERE `session` = %s", A_auth)
		if sqlObj is None:
			return False, HTTP_STATUS_CODES.ERROR_USER_SESSION_INVALID, None
		# Update session timestamp if session still in use
		Server.conn.execute("UPDATE `user_session` SET `timestamp` = CURRENT_TIMESTAMP() WHERE `session` = %s", A_auth)
		if (datetime.datetime.now() - sqlObj['timestamp']).total_seconds() > expire_day:
			return False, HTTP_STATUS_CODES.ERROR_USER_SESSION_EXPIRED, sqlObj
		return True, HTTP_STATUS_CODES.SUCCESS_VERIFY_SESSION, sqlObj

	def _do_GET(self):
		A_auth = self.headers.get('A-auth')

		if self.path == '/fetchNotification':
			if A_auth is None:
				user_id = 0
			else:
				sqlObj = Server.conn.query1("SELECT `user_id` FROM `user_session` WHERE `session` = %s", A_auth)
				if sqlObj is None:
					user_id = 0
				else:
					user_id = sqlObj['user_id']
			sqlObj = Server.conn.query("SELECT `title`, `body` FROM `notifications` WHERE "
				"(`affected_user` LIKE '%%{}%%' OR `affected_user` = 'all') AND `timestamp` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 DAY) ".format(user_id) +
				"ORDER BY `timestamp` DESC LIMIT 15")
			return HTTP_STATUS_CODES.SUCCESS_FETCH_NOTIFICATIONS([{'title': x['title'], 'body': x['body']} for x in sqlObj])

		return HTTP_STATUS_CODES.ERROR_400_BAD_REQUEST

	def _do_POST(self, jsonObject: dict):
		A_auth = self.headers.get('A-auth')

		# Process user login
		if self.path == '/login':
			sqlObj = Server.conn.query1("SELECT * FROM `accounts` WHERE `username` = %s", jsonObject['user'])
			if sqlObj is None:
				self.log_login_attmept(jsonObject['user'], False)
				return HTTP_STATUS_CODES.ERROR_INVALID_PASSWORD_OR_USER
			else:
				if sqlObj['password'] != jsonObject['password']:
					self.log_login_attmept(jsonObject['user'], False)
					return HTTP_STATUS_CODES.ERROR_INVALID_PASSWORD_OR_USER
				else:
					self.log_login_attmept(jsonObject['user'], True)
					session = self.generate_new_session_str(jsonObject['user'])
					Server.conn.execute("INSERT INTO `user_session` (`session`, `user_id`) VALUE (%s, %s)", (session, sqlObj['id']))
					return HTTP_STATUS_CODES.SUCCESS_LOGIN(jsonObject['user'], session)

		# Process register user
		elif self.path == '/register':
			if len(jsonObject['user']) > 16:
				return HTTP_STATUS_CODES.ERROR_USERNAME_TOO_LONG
			sqlObj = Server.conn.query1("SELECT * FROM `accounts` WHERE `username` = %s", jsonObject['user'])
			if sqlObj is None:
				Server.conn.execute("INSERT INTO `accounts` (`username`, `password`) VALUE (%s, %s)",
					(jsonObject['user'], jsonObject['password']))
				return HTTP_STATUS_CODES.SUCCESS_REGISTER
			else:
				return HTTP_STATUS_CODES.ERROR_USERNAME_ALREADY_EXIST

		# Process register firebase ID
		elif self.path == '/register_firebase':
			r, rt_value, sqlObj = self.verify_user_session(A_auth)
			if not r: return rt_value
			sqlObj1 = Server.conn.query1("SELECT `user_id` FROM `firebasetoken` WHERE `token` = %s", jsonObject['token'])
			if sqlObj1 is None:
				Server.conn.execute("INSERT INTO `firebasetoken` (`user_id`, `token`) VALUE (%s, %s)", (sqlObj['user_id'], jsonObject['token']))
			elif sqlObj['user_id'] != sqlObj1['user_id']:
				Server.conn.execute("UPDATE `firebasetoken` SET `user_id` = %s WHERE `token` = %s", (sqlObj['user_id'], jsonObject['token']))
			else:
				Server.conn.execute("UPDATE `firebasetoken` SET `register_date` = CURRENT_TIMESTAMP() WHERE `token` = %s", jsonObject['token'])
			return HTTP_STATUS_CODES.SUCCESS_REGISTER_FIREBASE_ID
		

		# Process verify user session string
		elif self.path == '/verify':
			_r, rt_value, _ = self.verify_user_session(A_auth)
			return rt_value
		
		# Process user logout
		elif self.path == '/logout':
			if A_auth is None:
				return HTTP_STATUS_CODES.ERROR_USER_SESSION_MISSING
			Server.conn.execute("DELETE FROM `user_session` WHERE `session` = %s", A_auth)
			return HTTP_STATUS_CODES.SUCCESS_LOGOUT

		elif self.path == '/admin':
			return self.handle_manage_request(jsonObject)

		return HTTP_STATUS_CODES.ERROR_INVALID_REQUEST

	def handle_manage_request(self, d: dict):
		# Not behind reversed proxy, bypass it
		if self.headers.get('X-Real-IP') and self.headers.get('X-Real-IP') not in Server.mdict['trust_ip']:
			return HTTP_STATUS_CODES.ERROR_403_FORBIDDEN
		if 't' not in d:
			return HTTP_STATUS_CODES.ERROR_400_BAD_REQUEST
		if d['t'] == 'firebase_post':
			if d['select_user'] == 'all':
				sqlObj = Server.conn.query("SELECT `token` FROM `firebasetoken` WHERE `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)")
				if len(sqlObj) == 1:
					r = Server.fcmbackend.push_services(sqlObj[0]['token'], d['title'], d['body'])
				else:
					r = Server.fcmbackend.push_services([x['token'] for x in sqlObj], d['title'], d['body'])
			else: # part of user
				devices = []
				for user in d['select_user']:
					sqlObj = Server.conn.quey("SELECT `token` FROM `firebasetoken` WHERE `user_id` = %s AND `register_date` > DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 2 MONTH)", user)
					devices.extend(x['token'] for x in sqlObj)
				if len(devices) == 1:
					r = Server.fcmbackend.push_services(devices[0], d['title'], d['body'])
				else:
					r = Server.fcmbackend.push_services(devices, d['title'], d['body'])
			if r['failure'] > 0:
				if r['success'] == 0:
					return HTTP_STATUS_CODES.ERROR_SEND_FIREBASE_NOTIFICATION_FAILURE
				else:
					return HTTP_STATUS_CODES.ERROR_SEND_FIREBASE_NOTIFICATION_PARTILAL_FAILURE
			u = ','.join(d['select_user']) if d['select_user'] != 'all' else 'all'
			Server.conn.execute("INSERT INTO `notifications` (`title`, `body`, `affected_user`) VALUE (%s, %s, %s)",
				(d['title'], d['body'], u))
			return HTTP_STATUS_CODES.SUCCESS_200OK
		return HTTP_STATUS_CODES.ERROR_400_BAD_REQUEST

	@staticmethod
	def generate_new_session_str(user_name: str):
		return ''.join(x.hexdigest() for x in map(
			hashlib.sha256, [
				os.urandom(16),
				str(time.time()).encode(),
				os.urandom(16),
				user_name.encode()
			]))

	def get_userid_from_username(self, user_name: str):
		sqlObj = Server.conn.query1("SELECT `id` FROM `accounts` WHERE `username` = %s AND `enabled` = 'Y'", user_name)
		if sqlObj is not None:
			return sqlObj['id']
		return None

class appServer:
	def __init__(self):
		self.config = ConfigParser()
		self.config.read('config.ini')

		self.mysql_conn = mysqldb(
			self.config['mysql']['host'],
			self.config['mysql']['user'],
			self.config['mysql']['password'],
			self.config['mysql']['database'],
			autocommit=True
		)

		self.mdict = {}
		if self.config.has_option('server', 'trust_ip'):
			self.mdict['trust_ip'] = list(map(str, self.config['server']['trust_ip'].split(',')))

		self.fcmService = fcmbackend.fcmService(self.config['firebase']['api_key'])

		setattr(Server, 'conn', self.mysql_conn)
		setattr(Server, 'mdict', self.mdict)
		setattr(Server, 'fcmbackend', self.fcmService)

		self.server_handle = HTTPServer(
			(self.config['server']['address'], int(self.config['server']['port'])),
			Server)

	def run(self):
		self.mysql_conn.do_keepalive()
		self.server_handle.serve_forever()

	def close(self):
		self.mysql_conn.close()

if __name__ == "__main__":
	appserver = appServer()
	try:
		appserver.run()
	except InterruptedError:
		appserver.close()
		raise
