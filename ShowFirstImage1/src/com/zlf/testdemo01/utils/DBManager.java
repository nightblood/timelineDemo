package com.zlf.testdemo01.utils;

import java.util.ArrayList;
import java.util.List;

import com.zlf.testdemo01.domain.ChatEntity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getReadableDatabase();
	}

	public void add(String name, String msg, String friendName) {
		db.execSQL("create table if not exists " + friendName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, data TEXT)");

		db.execSQL("insert into " + friendName + " values(null, ?, ?)", new Object[] { name, msg });
	}

	public List<ChatEntity> query(String friendName) {
		db.execSQL("create table if not exists " + friendName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, data TEXT)");
		Cursor cursor = db.rawQuery("select * from " + friendName, null);
		List<ChatEntity> datas = new ArrayList<ChatEntity>();
		while (cursor.moveToNext()) {
			ChatEntity entity = new ChatEntity(cursor.getString(cursor.getColumnIndex("name")), 
					cursor.getString(cursor.getColumnIndex("data")));
			datas.add(entity);
		}
		
		cursor.close();
		return datas;
	}
	
	public ChatEntity queryLatestMsg(String friendName) {
		db.execSQL("create table if not exists " + friendName
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, data TEXT)");
		Cursor cursor = db.rawQuery("select * from " + friendName + " order by _id desc limit 0,1", null);
		if (cursor.moveToNext()) {
			ChatEntity entity = new ChatEntity(cursor.getString(cursor.getColumnIndex("name")), 
					cursor.getString(cursor.getColumnIndex("data")));
			cursor.close();
			return entity;
		} else {
			cursor.close();
			return null;
		}
	}
	
	public void closeDB() {
		db.close();
	}

}
