package com.chipsguide.app.colorbluetoothlamp.v2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ApplicationDBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "alarm_color";
	private static final int VERSION = 1;

	public ApplicationDBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(AlarmLightColorBuilder.getAlarmLightColorSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	
}
