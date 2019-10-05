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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	Context context;
	private static final int DATABASE_VERSION = 4;
	private final static String DATABASE_NAME = "n0t1c3.db";
	private final static String TABLE_ACCOUNT = "Account";
	private final static String TABLE_OPTION = "Option";
	private final static String CREATE_ACCOUNT = "CREATE TABLE `Account` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `user` TEXT, `password` TEXT)";
	private final static String CREATE_OPTION = "CREATE TABLE `Option` (`name` TEXT PRIMARY KEY, `value` TEXT)";

	private final static String TAG = "log_Database";

	DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCOUNT);
		db.execSQL(CREATE_OPTION);
		db.execSQL("INSERT INTO `Option` (`name`, `value`) VALUES" +
				" (\"remember_password\", \"false\"), (\"session_string\", \"\"), (\"user_name\", \"\")");
		//init();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTION);
		onCreate(db);
	}

	boolean isRememberedPassword() {
		String sql = "SELECT * FROM `Option` WHERE `name` = \"remember_password\"";
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		cursor.moveToFirst();
		// https://stackoverflow.com/a/4088131
		boolean remember_password = cursor.getString(cursor.getColumnIndexOrThrow("value")).equals("true");
		cursor.close();
		return remember_password;
	}

	void setRememberedPassword(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("value", b? "true": "false");
		db.update(TABLE_OPTION, contentValues, "name = ?", new String[]{"value"});
		db.close();
	}

	void setSessionString(String sessionString) {
		SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("value", sessionString);
		sqLiteDatabase.update(TABLE_OPTION, contentValues, "name = ?", new String[]{"session_string"});
		sqLiteDatabase.close();
	}

	String getSessionString() {
		String sql = "SELECT `value` FROM `Option` WHERE `name` = \"session_string\"";
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		cursor.moveToFirst();
		String string = cursor.getString(cursor.getColumnIndexOrThrow("value"));
		cursor.close();
		return string;
	}

	String[] getStoredUser() {
		String[] accountGroup = new String[2];
		String sql = "SELECT * FROM `Account` ORDER BY `id` DESC LIMIT 1";
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

		if (cursor.getCount() == 0){
			accountGroup[0] = accountGroup[1] = "";
		}
		else {
			cursor.moveToFirst();
			accountGroup[0] = cursor.getString(cursor.getColumnIndexOrThrow("user"));
			accountGroup[1] = cursor.getString(cursor.getColumnIndexOrThrow("password"));
		}
		cursor.close();
		return accountGroup;
	}

	void updateUser(String user, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM `Account`");
		ContentValues contentValues = new ContentValues();
		contentValues.put("user", user);
		contentValues.put("password", password);
		db.insert(TABLE_ACCOUNT,null, contentValues);
		db.close();
	}

	DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

}