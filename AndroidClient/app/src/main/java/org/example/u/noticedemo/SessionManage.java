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

import androidx.annotation.NonNull;

public class SessionManage {
	private String firebaseID;
	private String userName;
	private String userSession;


	SessionManage(String _firebaseID, String _user_name, String _user_session){
		setFirebaseID(_firebaseID);
		setUserName(_user_name);
		setUserSession(_user_session);
	}

	SessionManage(String _firebaseID, DatabaseHelper db) {
		setFirebaseID(_firebaseID);
		getFromDatabase(db);
	}

	String getUserName() {
		return userName;
	}

	String getFirebaseID() {
		return firebaseID;
	}

	String getUserSession() {
		return userSession;
	}

	void getFromHttpRawResponse(HttpRawResponse hrr) {
		setUserName(hrr.getSessionUser());
		setUserSession(hrr.getSessionString());
	}

	void setUserName(String _userName) {
		userName = _userName;
	}

	void setUserSession(String _userSession) {
		userSession = _userSession;
	}

	void getFromDatabase(@NonNull DatabaseHelper db) {
		userName = db.getLoginedUser();
		userSession = db.getSessionString();
	}


	String setFirebaseID(String newFirebaseID) {
		firebaseID = newFirebaseID;
		return firebaseID;
	}

	NetworkRequestType getRequestParams() {
		return NetworkRequestType.generateVerifyParams(getUserName(), getUserSession());
	}

}
