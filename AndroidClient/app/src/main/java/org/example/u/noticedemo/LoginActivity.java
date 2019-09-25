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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

class NetworkLoginException extends Exception {

}

class NetworkRegisterException extends Exception {

}

public class LoginActivity extends AppCompatActivity {

		EditText etUser, etPassword, etRepeatPassword;
		Button btLogin, btGoRegister;
		TextView txtTitle, txtRepeatPassword;
		CheckBox cbRemember;

		ArrayList<TextWatcher> arrayList;
		String TAG = "log_LoginActivity";

		@Override
		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.init();
	}

	void vaild_username() {
		if (etUser.length() == 0)
			btGoRegister.setEnabled(false);
	}

	void setOnTextChangeListener() {

		etUser.addTextChangedListener(arrayList.get(0));

		// https://stackoverflow.com/a/20824665
		etPassword.addTextChangedListener(arrayList.get(1));

		etRepeatPassword.addTextChangedListener(arrayList.get(2));
	}

	void changeToRegisterLayout() {
		txtTitle.setText(getString(R.string.text_title_register_page));
		txtRepeatPassword.setVisibility(View.VISIBLE);
		etRepeatPassword.setVisibility(View.VISIBLE);
		cbRemember.setVisibility(View.INVISIBLE);
		//btLogin.setVisibility(View.INVISIBLE);

		btGoRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkSupport networkSupport;
				String strRegUser, strRegPassword;
				strRegUser = etUser.getText().toString();
				strRegPassword = etUser.getText().toString();

				if (strRegPassword.length() == 0) {
					strRegUser = "test";
					strRegPassword = "test";
				}

				try {
					networkSupport = new NetworkSupport(LoginActivity.this, null, NetworkRequestType.generateRegisterParams(strRegUser, strRegPassword));
					networkSupport.execute().get();
					HttpRawResponse httpRawResponse = JSONParser.networkJsonDecode(networkSupport.response);
					//HttpRawResponse httpRawResponse = networkSupport.doRegister(strRegUser, strRegPassword);

					if (httpRawResponse.getStatus() == 200) {
						//((EditText)findViewById(R.id.etUser)).setText(strRegUser);
						//((EditText)findViewById(R.id.etPassword)).setText(strRegPassword);
						Toast.makeText(LoginActivity.this, "Register success", Toast.LENGTH_SHORT).show();
						// https://stackoverflow.com/a/4038637
						backToLoginPage();
					}
					else {
						throw new NetworkRegisterException();
					}
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		});

		btLogin.setText(R.string.text_back_button);
		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backToLoginPage();
			}
		});
		setOnTextChangeListener();
	}

	void backToLoginPage() {
		txtTitle.setText(R.string.text_title_login_page);
		txtRepeatPassword.setVisibility(View.INVISIBLE);
		etRepeatPassword.setVisibility(View.INVISIBLE);
		//btLogin.setVisibility(View.VISIBLE);
		cbRemember.setVisibility(View.VISIBLE);

		etPassword.removeTextChangedListener(arrayList.get(1));

		btGoRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeToRegisterLayout();
			}
		});

		btLogin.setText(R.string.text_login);
		initbtnlogin();


	}

	void initbtnlogin() {

		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkSupport networkSupport;
				String strUser = etUser.getText().toString();
				String strPassword = etPassword.getText().toString();

				// DEBUG CODE
				if (strPassword.length() == 0) {
					strUser = "test";
					strPassword = "test";
				}

				HttpRawResponse httpRawResponse;
				try {
					networkSupport = new NetworkSupport(LoginActivity.this, null, NetworkRequestType.generateLoginParams(strUser, strPassword));
					//httpRawResponse = networkSupport.doLogin(strUser, strPassword);
					networkSupport.execute().get();
					httpRawResponse = JSONParser.networkJsonDecode(networkSupport.response);
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

	void init() {
		etUser = findViewById(R.id.etUser);
		etPassword = findViewById(R.id.etPassword);
		btLogin = findViewById(R.id.btLogin);
		cbRemember = findViewById(R.id.cbRemember);
		btGoRegister = findViewById(R.id.btGoRegister);

		txtTitle = findViewById(R.id.textLogin);
		txtRepeatPassword = findViewById(R.id.txtRepeatPassword);
		etRepeatPassword = findViewById(R.id.etRepeatPassword);

		etRepeatPassword.setVisibility(View.INVISIBLE);
		txtRepeatPassword.setVisibility(View.INVISIBLE);

		cbRemember.setChecked(MainActivity.databaseHelper.isRememberedPassword());

		if (cbRemember.isChecked()) {
			String[] accountGroup = MainActivity.databaseHelper.getStoredUser();
			etUser.setText(accountGroup[0]);
			etPassword.setText(accountGroup[1]);
		}

		initbtnlogin();

		btGoRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeToRegisterLayout();
				/*Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);*/
			}
		});
		appendTextWatcher();
	}

	void appendTextWatcher() {
		arrayList.add(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0)
					btGoRegister.setEnabled(false);
				else
				if (etPassword.length() > 0 &&
						etPassword.getText().toString().equals(etPassword.getText().toString()))
					btGoRegister.setEnabled(true);
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		arrayList.add(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Should use toString() method
				btGoRegister.setEnabled(etRepeatPassword.getText().toString().equals(s.toString()));
				vaild_username();
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		arrayList.add(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btGoRegister.setEnabled(etPassword.getText().toString().equals(s.toString()));
				vaild_username();
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

	}
}
