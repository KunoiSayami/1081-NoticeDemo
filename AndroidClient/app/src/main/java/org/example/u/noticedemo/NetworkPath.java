package org.example.u.noticedemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

public class NetworkPath {
	static String server_address = "";
	static String login_path = "";
	static String token_path = "";
	static String register_path = "";
	static String logout_path = "";
	static String verify_session_path = "";
	static String firebase_id_register_path = "";

	static final String TAG = "log_(static)networkPath";

	static void loadConfig(Context context) {
		//https://stackoverflow.com/a/45908819
		JSONObject[] jsonObjects = JSONParser.getJson(
				JSONParser.loadJSONFromAsset(context.getResources().openRawResource(R.raw.config))
		);
		if (jsonObjects != null) {
			try {
				server_address = jsonObjects[0].get(context.getString(R.string.server_address_field)).toString();
				login_path = jsonObjects[1].get(context.getString(R.string.login_field)).toString();
				token_path = jsonObjects[1].get(context.getString(R.string.token_field)).toString();
				register_path = jsonObjects[1].get(context.getString(R.string.register_field)).toString();
				verify_session_path = jsonObjects[1].get(context.getString(R.string.verify_session_field)).toString();
				firebase_id_register_path = jsonObjects[1].get(context.getString(R.string.firebase_id_register_field)).toString();
				logout_path = jsonObjects[1].get(context.getString(R.string.logout_field)).toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context, "loadConfig error!", Toast.LENGTH_LONG).show();
			Log.e(TAG, "loadConfig: jsonObjects can't be null");
			//https://support.crashlytics.com/knowledgebase/articles/112848-how-do-i-force-a-crash-using-the-android-sdk
			throw new RuntimeException("Read config error");
		}
	}
}
