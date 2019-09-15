package org.example.u.noticedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

class NetworkRegisterException extends Exception {

}

public class RegisterActivity extends AppCompatActivity {

	String TAG = "log_RegisterActivity";

	EditText etRegUser, etRegPassword;
	Button btRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		this.init();
	}

	void init() {
		etRegUser = findViewById(R.id.etRegUser);
		etRegPassword = findViewById(R.id.etRegPassword);
		btRegister = findViewById(R.id.btRegister);

		btRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkSupport networkSupport = new NetworkSupport();
				String strRegUser = etRegUser.getText().toString();
				String strRegPassword = etRegPassword.getText().toString();

				if (strRegPassword.length() == 0) {
					strRegUser = "test";
					strRegPassword = "test";
				}

				try {
					HttpRawResponse httpRawResponse =
					networkSupport.doRegister(strRegUser, strRegPassword);
					if (httpRawResponse.getStatus() == 200) {
						//((EditText)findViewById(R.id.etUser)).setText(strRegUser);
						//((EditText)findViewById(R.id.etPassword)).setText(strRegPassword);
						Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
						finish();
					}
					else {
						throw new NetworkRegisterException();
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}
}
