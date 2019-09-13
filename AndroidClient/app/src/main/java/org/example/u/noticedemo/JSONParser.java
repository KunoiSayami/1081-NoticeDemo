package org.example.u.noticedemo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class JSONParser {
	static String TAG = "NoticeDemoJSONParser";

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
			json = new String(buffer, "UTF-8");
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

	static public JSONObject[] networkJsonDecode(String json_text) {
		//Structure:
		// RAW | STATUS | OPTIONS | ERRORS
		JSONObject[] jsonObjects = new JSONObject[3];
		try{
			JSONObject obj = new JSONObject(json_text);
			jsonObjects[0] = obj;
			jsonObjects[1] = obj.getJSONObject("status");
			jsonObjects[2] = obj.getJSONObject("options");
			jsonObjects[3] = obj.getJSONObject("errors");
			return jsonObjects;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}
