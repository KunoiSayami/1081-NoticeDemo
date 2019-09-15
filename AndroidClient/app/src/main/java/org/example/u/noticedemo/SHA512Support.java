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
