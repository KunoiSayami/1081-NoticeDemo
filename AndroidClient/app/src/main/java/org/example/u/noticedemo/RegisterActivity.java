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

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

class NetworkRegisterException extends Exception {

}

public class RegisterActivity extends AppCompatActivity {

	String TAG = "log_RegisterActivity";

	EditText etRegUser, etRegPassword, etRegRepeatPassword;
	Button btRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		this.init();
	}

	void vaild_username() {
		if (etRegUser.length() == 0)
			btRegister.setEnabled(false);
	}

	void init() {
		etRegUser = findViewById(R.id.etRegUser);
		etRegPassword = findViewById(R.id.etRegPassword);
		etRegRepeatPassword = findViewById(R.id.etRegRepeatPassword);
		btRegister = findViewById(R.id.btRegister);
		btRegister.setEnabled(false);

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
						// https://stackoverflow.com/a/4038637
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

		etRegUser.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0)
					btRegister.setEnabled(false);
				else
					if (etRegPassword.length() > 0 &&
							etRegPassword.getText().toString().equals(etRegPassword.getText().toString()))
						btRegister.setEnabled(true);
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		// https://stackoverflow.com/a/20824665
		etRegPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// Should use toString() method
				btRegister.setEnabled(etRegRepeatPassword.getText().toString().equals(s.toString()));
				vaild_username();
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		etRegRepeatPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				btRegister.setEnabled(etRegPassword.getText().toString().equals(s.toString()));
				vaild_username();
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});
	}
}
