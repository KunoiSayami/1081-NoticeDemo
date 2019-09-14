package org.example.u.noticedemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HttpRawResponse {
	private int status;
	private JSONArray options;
	private JSONObject errors;
	HttpRawResponse(JSONObject jsonObject) throws JSONException {
		status = jsonObject.getInt("status");
		options = jsonObject.getJSONArray("options");
		errors = jsonObject.getJSONObject("errors");
	}

	int getStatus() {
		return status;
	}

	JSONArray getOptions() {
		return options;
	}

	JSONObject getErrors() {
		return getErrors();
	}
}
