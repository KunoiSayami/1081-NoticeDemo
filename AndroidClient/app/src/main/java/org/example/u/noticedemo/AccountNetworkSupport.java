/*
 ** Copyright (C) 2019 KunoiSayami
 **
 ** This file is part of 1081-NiceDemo and is released under
 ** the AGPL v3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 **
 ** This program is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU Affero General Public License as published by
 ** the Free Software Foundation, either version 3 of the License, or
 ** any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 ** GNU Affero General Public License for more details.
 **
 ** You should have received a copy of the GNU Affero General Public License
 ** along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.example.u.noticedemo;

import org.example.u.noticedemo.types.NetworkRequestType;

import java.security.NoSuchAlgorithmException;

import static org.example.u.noticedemo.NetworkPath.login_path;
import static org.example.u.noticedemo.NetworkPath.register_path;

public class AccountNetworkSupport extends Connect {
	private static final String TAG = "log_AccountNetworkSupport";
	AccountNetworkSupport(String user, String password, OnTaskCompleted o, boolean is_register)
			throws NoSuchAlgorithmException {
		super(
				is_register ?
						NetworkRequestType.generateRegisterParams(user, password) :
						NetworkRequestType.generateLoginParams(user, password),
				is_register? register_path : login_path,
				o
		);
	}

}
