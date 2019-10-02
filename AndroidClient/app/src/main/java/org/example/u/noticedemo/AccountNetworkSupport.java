package org.example.u.noticedemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.transform.Result;

public class AccountNetworkSupport extends NetworkSupportBase {
	private static final String TAG = "log_AccountNetworkSupport";
	private boolean _is_register;
	private String _user, _password;
	private OnTaskCompleted listener;
	AccountNetworkSupport(Context context, String user, String password, boolean is_register)
			throws NoSuchAlgorithmException {
		super(context, "",
				is_register ?
						NetworkRequestType.generateRegisterParams(user, password) :
						NetworkRequestType.generateLoginParams(user, password)
		);
		_is_register = is_register;
		_user = user;
		_password = password;
		chooseType();
	}

	private void chooseType(){
		postParams = networkRequestType.getParams();
		requestPath = _is_register? register_path : login_path;
	}


	public void Task(OnTaskCompleted listener) {
		this.listener = listener;
	}

	protected void onPostExecute(Long _reserved) {
		super.onPostExecute(_reserved);
		listener.onTaskCompleted(JSONParser.networkJsonDecode(response));
	}
}
