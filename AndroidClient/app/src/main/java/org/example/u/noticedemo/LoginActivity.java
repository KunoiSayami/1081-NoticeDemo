package org.example.u.noticedemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

	class NetworkLoginException extends Exception {

}

public class LoginActivity extends AppCompatActivity {

	EditText etUser, etPassword;
	Button btLogin;
	CheckBox cbRemember;

	String TAG = "log_LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.init();
	}

	void init() {
		etUser = findViewById(R.id.etUser);
		etPassword = findViewById(R.id.etPassword);
		btLogin = findViewById(R.id.btLogin);
		cbRemember = findViewById(R.id.cbRemember);

		cbRemember.setChecked(MainActivity.databaseHelper.isRememberedPassword());

		if (cbRemember.isChecked()) {
			String[] accountGroup = MainActivity.databaseHelper.getStoredUser();
			etUser.setText(accountGroup[0]);
			etPassword.setText(accountGroup[1]);
		}

		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkSupport networkSupport = new NetworkSupport();
				String strUser = etUser.getText().toString();
				String strPassword = etPassword.getText().toString();

				if (strPassword.length() == 0) {
					strUser = "test";
					strPassword = "test";
				}
				HttpRawResponse httpRawResponse;
				try {
					httpRawResponse = networkSupport.doLogin(strUser, strPassword);
					Log.d(TAG, "onClick: Status => " + httpRawResponse.getStatus());
					if (httpRawResponse.getStatus() == 200) {
						Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
						if (cbRemember.isChecked()){
							MainActivity.databaseHelper.updateUser(strUser, strPassword);
						}
					}
					else {
						throw new NetworkLoginException();
					}
					MainActivity.databaseHelper.setRememberedPassword(cbRemember.isChecked());
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(LoginActivity.this,"Login error", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
