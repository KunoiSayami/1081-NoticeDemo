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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


import org.example.u.noticedemo.listSupport.NotificationAdapter;
import org.example.u.noticedemo.types.FetchedNotificationArrayType;
import org.example.u.noticedemo.types.NotificationType;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.ackee.useragent.UserAgent;

public class MainActivity extends AppCompatActivity {

	static String TAG = "log_Main";
	static DatabaseHelper databaseHelper;
	private View.OnClickListener changeToLoginActivityListener, changeToLogoutListener;

	ListView lvNotices;

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

	void findView() {
		txtUserTitle = findViewById(R.id.txtUserTitle);
		btnLoginout = findViewById(R.id.btnLoginout);
		lvNotices = findViewById(R.id.lvNotices);
	}

	void init(){
		this.findView();
		MainActivity.databaseHelper = new DatabaseHelper(this);
		userSession = new SessionManage("", databaseHelper);

		// Tell user to wait checking session availability
		if (userSession.getUserSession().length() > 0) {
			txtUserTitle.setText(R.string.text_wait);
		}

		NetworkPath.loadConfig(MainActivity.this);
		Connect.setUserAgent(UserAgent.getInstance(this).getUserAgentString(""));

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		HelpMessageSupport.init_strings(MainActivity.this);

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
						reportFirebaseId(token);
						// Log and toast
						String msg = getString(R.string.msg_token_fmt, token);
						Log.d(TAG, msg);
						//Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});

		/*if (userSession.getUserSession().length() > 0)
			Toast.makeText(MainActivity.this, R.string.text_checking_login_status, Toast.LENGTH_SHORT).show();*/

		btnLoginout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (MainActivity.userSession.getUserSession().equals("")) {
					change_activity();
				}
				else {
					setLogoutListener();
				}
			}
		});
		btnLoginout.setEnabled(false);
		lvNotices.setVisibility(View.INVISIBLE);

		// https://stackoverflow.com/a/19026743
		accountEventReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				txtUserTitle.setText(String.format(getString(R.string.welcome_title_formatter), databaseHelper.getLoginedUser()));
				btnLoginout.setText(R.string.logout_text);
				btnLoginout.setOnClickListener(changeToLogoutListener);
				lvNotices.setVisibility(View.VISIBLE);
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(accountEventReceiver,
				new IntentFilter(getString(R.string.IntentFilter_login_success)));

		if (userSession.getUserSession().length() > 0) {
			new Connect(userSession.getRequestParams(),
					NetworkPath.verify_session_path,
					new OnTaskCompleted() {
						@Override
						public void onTaskCompleted(Object o) {
							HttpRawResponse h = (HttpRawResponse) o;
							if (h.getStatus() == 200) {
								txtUserTitle.setText(String.format(getString(R.string.welcome_title_formatter), userSession.getUserName()));
								btnLoginout.setText(R.string.logout_text);
								btnLoginout.setOnClickListener(changeToLogoutListener);
								lvNotices.setVisibility(View.VISIBLE);
							}
							else {
								txtUserTitle.setText(R.string.no_user_login_title);
								btnLoginout.setText(R.string.text_login);
								btnLoginout.setOnClickListener(changeToLoginActivityListener);
							}
							btnLoginout.setEnabled(true);
						}
					},
					true).execute();
		}
	}

	@Override
	protected void onResume() {
		new Connect(NetworkRequestType.generateFetchNotificationParams(userSession.toString()), NetworkPath.fetch_notification_path,
				new OnTaskCompleted() {
					@Override
					public void onTaskCompleted(Object o) {
						try {
							JSONArray jsonArray = new JSONArray(((HttpRawResponse)o).getOptions().get(0).toString());
							init_listView(new FetchedNotificationArrayType(jsonArray).getNotifications());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}).execute();
		super.onResume();
	}

	void init_listView(ArrayList<NotificationType> listArray) {
		final NotificationAdapter notificationAdapter = new NotificationAdapter(this, listArray);
		this.lvNotices.setAdapter(notificationAdapter);
		for (NotificationType nt: listArray) {
			notificationAdapter.add(nt);
		}
	}

	void setLogoutListener() {
		new Connect(
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
				}).execute();
	}

	static void reportFirebaseId(String firebaseID) {
		userSession.setFirebaseID(firebaseID);
		try {
			FirebaseNetworkSupport firebaseNetworkSupport = new FirebaseNetworkSupport(firebaseID);
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
