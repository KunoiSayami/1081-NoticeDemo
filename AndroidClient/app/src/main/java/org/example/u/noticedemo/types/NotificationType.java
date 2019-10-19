package org.example.u.noticedemo.types;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationType {
	private String title;
	private String body;
	public NotificationType(String _title, String _body){
		this.title = _title;
		this.body = _body;
	}
	public NotificationType(JSONObject j) throws JSONException {
		this.title = j.get("title").toString();
		this.body = j.get("body").toString();
	}

	public String getTitle() {
		return this.title;
	}

	public String getBodyShort() {
		if (getBody().length() > 50) {
			return getBody().substring(0, 50);
		}
		return getBody();
	}

	String getBody() {
		return this.body;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
