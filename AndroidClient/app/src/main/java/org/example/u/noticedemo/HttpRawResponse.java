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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class HttpRawResponse {
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
		return errors;
	}

	int getLastError() {
		try {
			return errors.getInt("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1;
	}

	String getErrorString() {
		try {
			return errors.getString("info");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "JSONException";
	}

	String getSessionUser() {
		try {
			return getOptions().getString(0);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return "ERROR";
	}

	String getSessionString() {
		try {
			return getOptions().getString(1);
		}
		catch (JSONException e){
			e.printStackTrace();
		}
		return "";
	}
}
