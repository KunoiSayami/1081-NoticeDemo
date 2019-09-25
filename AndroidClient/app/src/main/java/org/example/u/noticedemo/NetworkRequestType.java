package org.example.u.noticedemo;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import static org.example.u.noticedemo.SHA512Support.getHashedPassword;

/*
* This file defines network request type
* type:
* 	1. Login Event
*
* subType:
* 	0. Login event
* 	1. Register Event
*/

public class NetworkRequestType {
	int type, subType;
	HashMap<String, String> params;
	NetworkRequestType(int _type, int _subType, HashMap<String, String> _hashMap){
		type = _type;
		subType = _subType;
		params = _hashMap;
	}

	public int getType() {
		return type;
	}

	public int getSubType() {
		return subType;
	}

	public HashMap<String, String> getParams() {
		return params;
	}

	public static NetworkRequestType generateRegisterParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, 1);
	}

	public static NetworkRequestType generateLoginParams(String user, String password)
			throws NoSuchAlgorithmException{
		return _generateAccountAction(user, password, 0);
	}

	private
	static NetworkRequestType _generateAccountAction(String user, String password, int ActionType)
			throws NoSuchAlgorithmException {
		HashMap<String, String> params = new HashMap<>();
		params.put("user", user);
		params.put("password", getHashedPassword(password));
		return new NetworkRequestType(1, ActionType, params);
	}
}
