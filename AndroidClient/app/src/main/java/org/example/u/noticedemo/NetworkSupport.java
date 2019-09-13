package org.example.u.noticedemo;

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

	String doLogin(String user, String password) throws IOException {
		HashMap<String, String> params = new HashMap<>();
		params.put("user", user);
		params.put("password", password);
		return this.postData(params);
	}

	private String postData(HashMap<String, String> params) throws IOException {
		String response = "";
		URL url = new URL(server_address);
		HttpsURLConnection client = null;
		try {
			client = (HttpsURLConnection) url.openConnection();
			client.setRequestMethod("POST");
			client.setRequestProperty("Accept-Charset", "utf8");
			client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf8");

			client.setDoInput(true);
			client.setDoOutput(true);

			OutputStream os = client.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(os, StandardCharsets.UTF_8)
			);
			bufferedWriter.write(getPostDataString(params));

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
					response += line;
				}
			}
			else {
				response = "";
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
