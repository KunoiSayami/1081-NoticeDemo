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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

//https://stackoverflow.com/a/43146379
class NetworkSupport {

	static String server_address = "";
	static String login_path = "";
	static String token_path = "";
	static String register_path = "";

	static String TAG = "log_NetworkSupport";

	HttpRawResponse doUserAction(String user, String password, int actionType)
			throws IOException, NoSuchAlgorithmException {
		HashMap<String, String> params = new HashMap<>();
		params.put("user", user);
		params.put("password", SHA512Support.getHashedPassword(password));
		String path;
		switch (actionType) {
			case 0:
				path = login_path;
				break;
			case 1:
				path = register_path;
				break;
			default:
				throw new RuntimeException("Path has not default value");
		}
		String req = this.postData(path, params);
		Log.d(TAG, "doLogin: => " + req);
		return JSONParser.networkJsonDecode(req);
	}

	public
	HttpRawResponse doRegister(String user, String password)
		throws IOException, NoSuchAlgorithmException {
		return doUserAction(user, password, 1);
	}

	public
	HttpRawResponse doLogin(String user, String password)
			throws IOException, NoSuchAlgorithmException {
		return doUserAction(user, password, 0);
	}

	private
	String postData(String path, HashMap<String, String> params)
			throws IOException {
		String response = "";
		String strParams = new JSONObject(params).toString();
		StringBuilder stringBuilder = new StringBuilder();
		URL url = new URL(server_address + path);
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
