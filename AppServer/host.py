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

	def getJsonObject(self, input_json: dict):
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
					return generate_dict.SUCCESS_LOGIN(self.generate_new_session_str(input_json['user']))
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
		elif self.path == '/register_firebase':
			sqlObj = Server.conn.execute("SELECT `user_id` FROM `user_session` WHERE `session` = %s", self.headers.get('auth'))
			if sqlObj is None:
				return generate_dict.ERROR_USER_SESSION_INVALID()
			if (datetime.datetime.now() - sqlObj['timestamp']).total_seconds() > expire_day:
				return generate_dict.ERROR_USER_SESSION_EXPIRED()
			sqlObj1 = Server.conn.query1("SELECT `user_id` FROM `firebasetoken` WHERE `token` = %s", input_json['token'])
			if sqlObj1 is None:
				Server.conn.execute("INSERT INTO `firebasetoken` (`user_id`, `token`) VALUE (%s, %s)", (sqlObj['user_id'], input_json['token']))
			elif sqlObj['user_id'] != sqlObj1['user_id']:
				Server.conn.execute("UPDATE `firebasetoken` SET `user_id` = %s WHERE `token` = %s", (sqlObj['user_id'], input_json['token']))
			else:
				Server.conn.execute("UPDATE `firebasetoken` SET `register_date` = CURRENT_TIMESTAMP() WHERE `token` = %s", input_json['token'])
			return generate_dict.SUCCESS_REGISTER_FIREBASE_ID()

	def generate_new_session_str(self, user_name: str):
		return ''.join(x.hexdigest() for x in map(
			hashlib.sha256, [
				os.urandom(16),
				str(time.time()).encode(),
				os.urandom(16),
				user_name.encode()
			]))

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
