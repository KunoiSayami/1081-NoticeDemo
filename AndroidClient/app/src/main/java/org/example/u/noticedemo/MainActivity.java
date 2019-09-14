package org.example.u.noticedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	String CHANNEL_ID = "NoticeDemo";

	String TAG = "NoticeDemoMain";

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
				NetworkSupport.server_address = jsonObjects[0].get(getString(R.string.server_address_field)).toString();
				NetworkSupport.login_path = jsonObjects[1].get(getString(R.string.login_field)).toString();
				NetworkSupport.token_path = jsonObjects[1].get(getString(R.string.token_field)).toString();
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

						// Log and toast
						String msg = getString(R.string.msg_token_fmt, token);
						Log.d(TAG, msg);
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				});

	}

}
