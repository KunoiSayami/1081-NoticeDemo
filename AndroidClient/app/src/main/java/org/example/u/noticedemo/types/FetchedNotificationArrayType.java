package org.example.u.noticedemo.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FetchedNotificationArrayType {
	ArrayList<NotificationType> notifications;
	public FetchedNotificationArrayType(JSONArray j) {
		for (int i = 0; i < j.length(); i++){
			try {
				notifications.add(new NotificationType((JSONObject) j.get(i)));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<NotificationType> getNotifications() {
		return notifications;
	}
}
