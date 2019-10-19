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

class FirebaseNetworkSupport extends Connect {
	FirebaseNetworkSupport(String firebase_id) {
		super(NetworkRequestType.generateRegisterFirebaseIDParams(
				firebase_id,
				MainActivity.userSession.getUserSession()
		), NetworkPath.firebase_id_register_path, null);
	}
}
