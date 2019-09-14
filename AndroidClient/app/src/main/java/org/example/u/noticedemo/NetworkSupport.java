package org.example.u.noticedemo;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

//https://stackoverflow.com/a/43146379
class NetworkSupport {

	static String server_address = "";
	static String login_path = "";
	static String token_path = "";


	static String TAG = "NoticeDemoNetworkSupport";

	private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
		StringBuilder feedback = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> entry : params.entrySet()){
			if (first)
				first = false;
			else
				feedback.append("&");

			feedback.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
			feedback.append("=");
			feedback.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
		}

		return feedback.toString();
	}

	public HttpRawResponse doLogin(String user, String password) throws IOException {
		HashMap<String, String> params = new HashMap<>();
		params.put("user", user);
		params.put("password", password);
		String req = this.postData(params);
		Log.d(TAG, "doLogin: => " + req);
		return JSONParser.networkJsonDecode(req);
	}

	private String postData(HashMap<String, String> params) throws IOException {
		String response = "";
		String strParams = new JSONObject(params).toString();
		StringBuilder stringBuilder = new StringBuilder();
		URL url = new URL(server_address);
		HttpsURLConnection client = null;
		try {
			client = (HttpsURLConnection) url.openConnection();
			client.setRequestMethod("POST");
			client.setRequestProperty("Accept-Charset", "utf8");
			client.setRequestProperty("Content-Type", "application/json");

			client.setDoInput(true);
			client.setDoOutput(true);

			OutputStream os = client.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(os, StandardCharsets.UTF_8)
			);
			bufferedWriter.write(strParams);

			bufferedWriter.flush();
			bufferedWriter.close();
			os.close();

			int responseCode = client.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(client.getInputStream())
				);
				while ((line = bufferedReader.readLine()) != null){
					Log.d(TAG, "postData: line => " + line);
					stringBuilder.append(line);
					//response += line;
				}
				response = stringBuilder.toString();
				//response = response.substring(1, response.length() - 1);
				bufferedReader.close();
			}
			else {
				response = "{}";
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally {
			if (client != null)
				client.disconnect();
		}
		return response;
	}
}
