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

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

	String TAG = "log_Main";
	static DatabaseHelper databaseHelper;
	static String user_auth = "";
	static String firebase_id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		this.loadConfig();
		this.init();
		this.change_activity();
	}

	void change_activity() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
	}

	void loadConfig() {
		//https://stackoverflow.com/a/45908819
		JSONObject[] jsonObjects = JSONParser.getJson(
			JSONParser.loadJSONFromAsset(getApplicationContext().getResources().openRawResource(R.raw.config))
		);
		if (jsonObjects != null) {
			try {
				NetworkSupportBase.server_address = jsonObjects[0].get(getString(R.string.server_address_field)).toString();
				NetworkSupportBase.login_path = jsonObjects[1].get(getString(R.string.login_field)).toString();
				NetworkSupportBase.token_path = jsonObjects[1].get(getString(R.string.token_field)).toString();
				NetworkSupportBase.register_path = jsonObjects[1].get(getString(R.string.register_field)).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(MainActivity.this, "loadConfig error!", Toast.LENGTH_LONG).show();
			Log.e(TAG, "loadConfig: jsonObjects can't be null");
			//https://support.crashlytics.com/knowledgebase/articles/112848-how-do-i-force-a-crash-using-the-android-sdk
			throw new RuntimeException("Read config error");
		}
	}

	void init(){
		MainActivity.databaseHelper = new DatabaseHelper(this);
		user_auth = databaseHelper.getSessionString();
		FirebaseInstanceId.getInstance().getInstanceId()
				.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
					@Override
					public void onComplete(@NonNull Task<InstanceIdResult> task) {
						if (!task.isSuccessful()) {
							Log.w(TAG, "getInstanceId failed", task.getException());
							return;
						}

						// Get new Instance ID token
						String token = task.getResult().getToken();
						firebase_id = token;
						// Log and toast
						String msg = getString(R.string.msg_token_fmt, token);
						Log.d(TAG, msg);
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});
		try {
			FirebaseNetworkSupport firebaseNetworkSupport = new FirebaseNetworkSupport(MainActivity.this, firebase_id);
			firebaseNetworkSupport.execute();
			Log.d(TAG, "init: Register firebase id successful");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onDestroy() {
		//CheckBox cbRemember = findViewById(R.id.cbRemember);
		//MainActivity.databaseHelper.setRememberedPassword(cbRemember.isChecked());
		MainActivity.databaseHelper.close();
		super.onDestroy();
	}
}
