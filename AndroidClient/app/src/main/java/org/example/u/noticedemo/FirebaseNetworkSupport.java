package org.example.u.noticedemo;

import android.content.Context;

class FirebaseNetworkSupport extends NetworkSupportBase {
	FirebaseNetworkSupport(Context context, String firebase_id) {
		super(context, null, NetworkRequestType.generateRegisterFirebaseIDParams(
				firebase_id,
				MainActivity.user_auth
		));
	}
}
