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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// https://stackoverflow.com/a/46510436
class SHA512Support {
	static String getHashedPassword(String passwordToHash) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] digest = md.digest(passwordToHash.getBytes());
		StringBuilder stringBuilder = new StringBuilder();
		for (byte i : digest.clone() ) {
			stringBuilder.append(Integer.toString((i & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuilder.toString();
	}
}
