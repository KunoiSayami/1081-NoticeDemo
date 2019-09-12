package org.example.u.noticedemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

	String CHANNEL_ID = "NoticeDemo";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.sendNotice();
	}

	void sendNotice(){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.common_full_open_on_phone)
				.setContentTitle("Test")
				.setContentText("test demo")
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);

		NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
		notificationManagerCompat.notify(1, builder.build());
	}

}
