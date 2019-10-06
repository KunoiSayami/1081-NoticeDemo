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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {

	static String TAG = "log_Main";
	static DatabaseHelper databaseHelper;
	private View.OnClickListener changeToLoginActivityListener, changeToLogoutListener;

	static SessionManage userSession;

	BroadcastReceiver accountEventReceiver;
	TextView txtUserTitle;
	Button btnLoginout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.pre_init();
		this.init();
		//this.change_activity();
	}

	void pre_init() {
		changeToLoginActivityListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				change_activity();
			}
		};

		changeToLogoutListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setLogoutListener();
			}
		};
	}

	void change_activity() {
		Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		startActivity(intent);
	}

	void init(){
		MainActivity.databaseHelper = new DatabaseHelper(this);
		userSession = new SessionManage("", databaseHelper);
		Log.d(TAG, "init: user_session_string => " + userSession.getUserSession().length());
		NetworkPath.loadConfig(MainActivity.this);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HelpMessageSupport.init_strings(MainActivity.this);

		txtUserTitle = findViewById(R.id.txtUserTitle);
		btnLoginout = findViewById(R.id.btnLoginout);

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
						reportFirebaseId(MainActivity.this, token);
						// Log and toast
						String msg = getString(R.string.msg_token_fmt, token);
						Log.d(TAG, msg);
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});

		btnLoginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainActivity.userSession.getUserSession().equals("")) {
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				else {
					setLogoutListener();
				}
			}
		});

		// https://stackoverflow.com/a/19026743
		accountEventReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				txtUserTitle.setText(String.format(getString(R.string.welcome_title_formatter), databaseHelper.getLoginedUser()));
				btnLoginout.setText(R.string.logout_text);
				btnLoginout.setOnClickListener(changeToLogoutListener);
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(accountEventReceiver,
				new IntentFilter(getString(R.string.IntentFilter_login_success)));

		if (userSession.getUserSession().length() > 0) {
			new NetworkSupportBase(MainActivity.this, null,
					userSession.getRequestParams(),
					NetworkPath.verify_session_path,
					new OnTaskCompleted() {
						@Override
						public void onTaskCompleted(Object o) {
							HttpRawResponse h = (HttpRawResponse) o;
							if (h.getStatus() == 200) {
								txtUserTitle.setText(String.format(getString(R.string.welcome_title_formatter), userSession.getUserName()));
								btnLoginout.setText(R.string.logout_text);
								btnLoginout.setOnClickListener(changeToLogoutListener);
							}
							else {
								txtUserTitle.setText(R.string.no_user_login_title);
								btnLoginout.setText(R.string.text_login);
								btnLoginout.setOnClickListener(changeToLoginActivityListener);
							}
						}
					}).execute();
		}
	}

	void setLogoutListener() {
		NetworkSupportBase l = new NetworkSupportBase(MainActivity.this, null,
				NetworkRequestType.generateLogoutParams(userSession.getUserSession()), NetworkPath.logout_path,
				new OnTaskCompleted() {
					@Override
					public void onTaskCompleted(Object o) {
						txtUserTitle.setText(R.string.no_user_login_title);
						btnLoginout.setText(R.string.text_login);
						btnLoginout.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(MainActivity.this, LoginActivity.class);
								startActivity(intent);
							}
						});
					}
				});
		l.execute();
	}

	static void reportFirebaseId(Context context, String firebaseID) {
		userSession.setFirebaseID(firebaseID);
		try {
			FirebaseNetworkSupport firebaseNetworkSupport = new FirebaseNetworkSupport(context, firebaseID);
			firebaseNetworkSupport.execute();
			Log.d(TAG, "init: Register firebase id successful");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		MainActivity.databaseHelper.close();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(accountEventReceiver);
		super.onDestroy();
	}
}
