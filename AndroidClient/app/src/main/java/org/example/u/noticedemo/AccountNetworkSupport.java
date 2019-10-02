package org.example.u.noticedemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

public class AccountNetworkSupport extends NetworkSupportBase {
	private static final String TAG = "log_AccountNetworkSupport";
	private boolean _is_register;
	private String _user, _password;
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

	// If finished, call this function
	@Override
	void callback() {
		try {
			HttpRawResponse httpRawResponse = JSONParser.networkJsonDecode(response);
			Log.d(TAG, "register_callback: Status => " + httpRawResponse.getStatus());
			if (this._is_register)
				register_callback(httpRawResponse);
			else
				login_callback(httpRawResponse);
		}
		catch (Exception e){
			e.printStackTrace();
			Toast.makeText(myContext,  (_is_register ? "Register": "Login")+" error", Toast.LENGTH_SHORT).show();
		}
	}


	void register_callback(HttpRawResponse httpRawResponse) {
		if (httpRawResponse.getStatus() == HttpsURLConnection.HTTP_OK) {
			Toast.makeText(myContext, "Register success", Toast.LENGTH_SHORT).show();
			((LoginActivity)myContext).backToLoginPage();
		}
		else {
			// ERROR PROCESS GOES HERE
		}

	}

	void login_callback(HttpRawResponse httpRawResponse) {
		if (httpRawResponse.getStatus() == HttpsURLConnection.HTTP_OK) {
			Toast.makeText(myContext, "Login success", Toast.LENGTH_SHORT).show();
			if (((LoginActivity)myContext).cbRemember.isChecked()) {
				MainActivity.databaseHelper.updateUser(_user, _password);
			}
			else {
				// ERROR PROCESS GOES HERE
			}
		}
		MainActivity.databaseHelper.setRememberedPassword(((LoginActivity)myContext).cbRemember.isChecked());
	}
}
