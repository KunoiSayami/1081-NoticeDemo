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
*/

class enumMajorType {
	final static int ACCOUNT = 1;
	final static int FIREBASE = 2;
}

class enumMinorType {
	static class ACCOUNT {
		final static int LOGIN = 0;
		final static int REGISTER = 1;
	}
	static class FIREBASE {
		final static int REGISTER = 0;
	}
}

public class NetworkRequestType {
	int type, subType;
	HashMap<String, String> params, headers;
	NetworkRequestType(int _type, int _subType, HashMap<String, String> _hashMap, HashMap<String, String> _headers){
		type = _type;
		subType = _subType;
		params = _hashMap;
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

	static NetworkRequestType generateRegisterParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, enumMinorType.ACCOUNT.REGISTER);
	}

	static NetworkRequestType generateLoginParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, enumMinorType.ACCOUNT.LOGIN);
	}

	public static NetworkRequestType generateRegisterFirebaseIDParams(String firebaseID, String sessionStr){
		HashMap<String, String> params = new HashMap<>();
		params.put("firebaseID", firebaseID);
		HashMap<String, String> _headers = new HashMap<>();
		_headers.put("auth", sessionStr);
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
