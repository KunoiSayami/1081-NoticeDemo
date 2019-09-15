package org.example.u.noticedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	Context context;
	private static final int DATABASE_VERSION = 1;
	private static String DATABASE_NAME = "n0t1c3.db";
	private static String TABLE_ACCOUNT = "Account";
	private static String TABLE_OPTION = "Option";
	private static String CREATE_ACCOUNT = "CREATE TABLE `Account` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `user` TEXT, `password` TEXT)";
	private static String CREATE_OPTION = "CREATE TABLE `Option` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `remember_password` BOOLEAN)";
	public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	/*void init() {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("remember_password", false);
		db.insert(TABLE_OPTION, null, contentValues);;
		db.close();
	}*/

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCOUNT);
		db.execSQL(CREATE_OPTION);
		db.execSQL("INSERT INTO `Option` (`remember_password`) VALUES (0)");
		//init();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTION);
		onCreate(db);
	}

	public boolean isRememberedPassword() {
		String sql = "SELECT * FROM `Option` LIMIT 1";
		SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
		cursor.moveToFirst();
		// https://stackoverflow.com/a/4088131
		boolean remember_password = cursor.getInt(cursor.getColumnIndexOrThrow("remember_password")) > 0;
		cursor.close();
		return remember_password;
	}

	public void setRememberedPassword(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("remember_password", false);
		db.update(TABLE_OPTION, contentValues, "id = 1",null);
		db.close();
	}

	public String[] getStoredUser() {
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

	public void updateUser(String user, String password) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM `Account`");
		ContentValues contentValues = new ContentValues();
		contentValues.put("user", user);
		contentValues.put("password", password);
		db.insert(TABLE_ACCOUNT,null, contentValues);
		db.close();
	}

	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

}