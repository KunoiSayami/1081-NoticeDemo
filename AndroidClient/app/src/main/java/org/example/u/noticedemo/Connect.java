package org.example.u.noticedemo;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


import static org.example.u.noticedemo.NetworkPath.server_address;

public class Connect extends AsyncTask<URL, Integer, Long> {
	private static final String TAG = "log_Connect";

	private String response = "";
	private String method, requestPath;
	static private String userAgent;


	private OnTaskCompleted listener;

	private HashMap<String, String> headerParams, postParams;


	Connect(HashMap<String, String> _headerParams,
			HashMap<String, String> _postParams,
			String _requestPath,
			OnTaskCompleted _listener,
			boolean is_post) {
		headerParams = _headerParams;
		postParams = _postParams;
		requestPath = _requestPath;
		listener = _listener;
		method = is_post? "POST" : "GET";
	}

	Connect(NetworkRequestType nrt,
			String _requestPath,
			OnTaskCompleted _listener) {
		headerParams = nrt.getHeaders();
		postParams = nrt.getParams();
		requestPath = _requestPath;
		listener = _listener;
		method = "POST";
	}

	Connect(NetworkRequestType nrt,
			String _requestPath,
			OnTaskCompleted _listener,
			boolean is_post) {
		headerParams = nrt.getHeaders();
		postParams = nrt.getParams();
		requestPath = _requestPath;
		listener = _listener;
		method = is_post? "POST" : "GET";
	}

	private
	void doConnect() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		URL url = new URL(server_address + requestPath);
		HttpsURLConnection client = null;
		try {
			client = (HttpsURLConnection) url.openConnection();
			client.setRequestMethod(method);

			client.setRequestProperty("Accept-Charset", "utf8");
			client.setRequestProperty("Content-Type", "application/json");
			client.setRequestProperty("User-Agent", userAgent);

			if (headerParams != null) {
				Iterator it = headerParams.entrySet().iterator();
				while (it.hasNext()) {
					HashMap.Entry pair = (HashMap.Entry)it.next();
					client.setRequestProperty((String)pair.getKey(), (String)pair.getValue());
					it.remove();
				}
			}

			client.setDoInput(true);

			if (method.equals("POST")) {
				client.setDoOutput(true);
				String strParams = new JSONObject(postParams).toString();
				OutputStream os = client.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(os, StandardCharsets.UTF_8)
				);
				bufferedWriter.write(strParams);

				bufferedWriter.flush();
				bufferedWriter.close();
				os.close();
			}

			int responseCode = client.getResponseCode();
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				String line;
				BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(client.getInputStream())
				);
				while ((line = bufferedReader.readLine()) != null){
					//Log.d(TAG, "postData: line => " + line);
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
		catch (MalformedURLException e){
			e.printStackTrace();
		}
		finally {
			if (client != null)
				client.disconnect();
			//Log.d(TAG, "postData: Finish");
		}
		//Log.d(TAG, "postData: Finish Response => " + response);
	}

	static
	void setUserAgent(String _userAgent) {
		userAgent = _userAgent;
	}

	@Override
	protected Long doInBackground(URL... params) {
		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		final byte[] result = response.getBytes();
		return (long) result.length;
	}

	protected void onPostExecute(Long _reserved) {
		super.onPostExecute(_reserved);
		if (listener != null)
			listener.onTaskCompleted(JSONParser.networkJsonDecode(response));
	}
}
