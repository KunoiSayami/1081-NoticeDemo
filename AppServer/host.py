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
import generate_dict

expire_day = 2 * 60 * 60 * 24

class Server(_exServer):
	def log_login_attmept(self, user: str, b: bool):
		Server.conn.execute("INSERT INTO `log_login` (`attempt_user`, `success`) VALUE (%s, %s)", (user, 'Y' if b else 'N'))
		if b:
			Server.conn.execute("UPDATE `accounts` SET `last_login` = CURRENT_TIMESTAMP() WHERE `username` = %s", user)

	def verify_user_session(self, A_auth: str):
		if A_auth is None:
			return False, generate_dict.ERROR_USER_SESSION_MISSING(), None
		sqlObj = Server.conn.query1("SELECT `user_id`, `timestamp` FROM `user_session` WHERE `session` = %s", A_auth)
		if sqlObj is None:
			return False, generate_dict.ERROR_USER_SESSION_INVALID(), None
		if (datetime.datetime.now() - sqlObj['timestamp']).total_seconds() > expire_day:
			return False, generate_dict.ERROR_USER_SESSION_EXPIRED(), sqlObj
		return True, generate_dict.SUCCESS_VERIFY_SESSION(), sqlObj

	def getJsonObject(self, input_json: dict):
		A_auth = self.headers.get('A-auth')

		# Process user login
		if self.path == '/login':
			sqlObj = Server.conn.query1("SELECT * FROM `accounts` WHERE `username` = %s", input_json['user'])
			if sqlObj is None:
				self.log_login_attmept(input_json['user'], False)
				return generate_dict.ERROR_INVALID_PASSWORD_OR_USER()
			else:
				if sqlObj['password'] != input_json['password']:
					self.log_login_attmept(input_json['user'], False)
					return generate_dict.ERROR_INVALID_PASSWORD_OR_USER()
				else:
					self.log_login_attmept(input_json['user'], True)
					session = self.generate_new_session_str(input_json['user'])
					Server.conn.execute("INSERT INTO `user_session` (`session`, `user_id`) VALUE (%s, %s)", (session, sqlObj['id']))
					return generate_dict.SUCCESS_LOGIN(input_json['user'], session)

		# Process register user
		elif self.path == '/register':
			if len(input_json['user']) > 16:
				return generate_dict.ERROR_USERNAME_TOO_LONG()
			sqlObj = Server.conn.query1("SELECT * FROM `accounts` WHERE `username` = %s", input_json['user'])
			if sqlObj is None:
				Server.conn.execute("INSERT INTO `accounts` (`username`, `password`) VALUE (%s, %s)",
					(input_json['user'], input_json['password']))
				return generate_dict.SUCCESS_REGISTER()
			else:
				return generate_dict.ERROR_USERNAME_ALREADY_EXIST()

		# Process register firebase ID
		elif self.path == '/register_firebase':
			r, rt_value, sqlObj = self.verify_user_session(A_auth)
			if not r: return rt_value
			sqlObj1 = Server.conn.query1("SELECT `user_id` FROM `firebasetoken` WHERE `token` = %s", input_json['token'])
			if sqlObj1 is None:
				Server.conn.execute("INSERT INTO `firebasetoken` (`user_id`, `token`) VALUE (%s, %s)", (sqlObj['user_id'], input_json['token']))
			elif sqlObj['user_id'] != sqlObj1['user_id']:
				Server.conn.execute("UPDATE `firebasetoken` SET `user_id` = %s WHERE `token` = %s", (sqlObj['user_id'], input_json['token']))
			else:
				Server.conn.execute("UPDATE `firebasetoken` SET `register_date` = CURRENT_TIMESTAMP() WHERE `token` = %s", input_json['token'])
			return generate_dict.SUCCESS_REGISTER_FIREBASE_ID()
		

		# Process verify user session string
		elif self.path == '/verify':
			_r, rt_value, _ = self.verify_user_session(A_auth)
			return rt_value
		
		# Process user logout
		elif self.path == '/logout':
			if A_auth is None:
				return generate_dict.ERROR_USER_SESSION_MISSING()
			Server.conn.execute("DELETE FROM `user_session` WHERE `session` = %s", A_auth)
			return generate_dict.SUCCESS_LOGOUT()

		return generate_dict.ERROR_INVALID_REQUEST()


	def handle_manage_request(self):
		pass

	def generate_new_session_str(self, user_name: str):
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

		setattr(Server, 'conn', self.mysql_conn)
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
