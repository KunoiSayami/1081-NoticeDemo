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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class JSONParser {
	static String TAG = "log_JSONParser";

	/* Reference material:
	https://blog.csdn.net/bzlj2912009596/article/details/79223818
	https://howtodoinjava.com/library/json-simple-read-write-json-examples/
	https://stackoverflow.com/a/19945484
	https://stackoverflow.com/questions/2591098/how-to-parse-json-in-java
	http://theoryapp.com/parse-json-in-java/
	*/
	static public String loadJSONFromAsset(InputStream is) {
		String json;
		try {
			//InputStream is = getActivity().getAssets().open("yourfilename.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, StandardCharsets.UTF_8);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		return json;
	}

	static public JSONObject[] getJson(String json_text) {
		JSONObject[] jsonObjects = new JSONObject[2];
		try {
			JSONObject obj = new JSONObject(json_text);
			JSONObject pageObj = obj.getJSONObject("pages");
			jsonObjects[0] = obj;
			jsonObjects[1] = pageObj;
			return jsonObjects;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "getJson: Error while parse json object");
		}
		return null;
	}
	

	static public HttpRawResponse networkJsonDecode(String json_text) {
		//Structure:
		// RAW | STATUS | OPTIONS | ERRORS
		HttpRawResponse httpRawResponse;
		Log.d(TAG, "networkJsonDecode: json_text => "+ json_text);
		try{
			JSONObject obj = new JSONObject(json_text);
			httpRawResponse = new HttpRawResponse(obj);
			return httpRawResponse;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
