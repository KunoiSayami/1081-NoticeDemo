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

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.example.u.noticedemo.SHA512Support.getHashedPassword;

/*
* This file defines network request type
* type:
* 	1. Login Event
*	2. FirebaseID Event
* subType:
*	1:
* 	  0. Login event
*	  1. Register Event
* 	  2. Verify session
*/

class enumMajorType {
	final static int ACCOUNT = 1;
	final static int FIREBASE = 2;
}

class enumMinorType {
	static class ACCOUNT {
		final static int LOGIN = 0;
		final static int REGISTER = 1;
		final static int VERIFY = 2;
		final static int LOGOUT = 3;
	}
	static class FIREBASE {
		final static int REGISTER = 0;
	}
}

public class NetworkRequestType {
	private int type, subType;
	private HashMap<String, String> params, headers;
	NetworkRequestType(int _type, int _subType, HashMap<String, String> _params, HashMap<String, String> _headers){
		type = _type;
		subType = _subType;
		params = _params;
		if (params == null) {
			params = new HashMap<>();
		}
		headers = _headers;
	}

	public int getType() {
		return type;
	}

	public int getSubType() {
		return subType;
	}

	HashMap<String, String> getParams() {
		return params;
	}

	HashMap<String, String> getHeaders() {
		return headers;
	}

	static NetworkRequestType generateLogoutParams(String session_string) {
		HashMap<String, String> _headers = new HashMap<>();
		_headers.put("A-auth", session_string);
		return new NetworkRequestType(enumMajorType.ACCOUNT, enumMinorType.ACCOUNT.LOGOUT, null, _headers);
	}

	static NetworkRequestType generateVerifyParams(String user, String session_string) {
		HashMap<String, String> _headers = new HashMap<>();
		_headers.put("A-user", user);
		_headers.put("A-auth", session_string);
		return new NetworkRequestType(enumMajorType.ACCOUNT, enumMinorType.ACCOUNT.VERIFY, null, _headers);
	}

	static NetworkRequestType generateRegisterParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, enumMinorType.ACCOUNT.REGISTER);
	}

	static NetworkRequestType generateLoginParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, enumMinorType.ACCOUNT.LOGIN);
	}

	static NetworkRequestType generateRegisterFirebaseIDParams(String firebaseID, String sessionStr){
		HashMap<String, String> params = new HashMap<>();
		params.put("token", firebaseID);
		HashMap<String, String> _headers = new HashMap<>();
		_headers.put("A-auth", sessionStr);
		return new NetworkRequestType(enumMajorType.FIREBASE, enumMinorType.FIREBASE.REGISTER, params, _headers);
	}

	private
	static NetworkRequestType _generateAccountAction(String user, String password, int ActionType)
			throws NoSuchAlgorithmException {
		HashMap<String, String> params = new HashMap<>();
		params.put("user", user);
		params.put("password", getHashedPassword(password));
		return new NetworkRequestType(enumMajorType.ACCOUNT, ActionType, params, null);
	}
}
