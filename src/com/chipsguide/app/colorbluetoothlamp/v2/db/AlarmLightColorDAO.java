package com.chipsguide.app.colorbluetoothlamp.v2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.AlarmClock;

import com.chipsguide.app.colorbluetoothlamp.v2.bean.AlarmLightColor;
import com.chipsguide.lib.timer.Alarm;
import com.chipsguide.lib.timer.db.AlarmBuilder;

public class AlarmLightColorDAO {
	public static final String TABLE_NAME = "lightColor";
	private static AlarmLightColorDAO dao;
	private ApplicationDBHelper dbHelper;
	private SQLiteDatabase database;

	private AlarmLightColorDAO(Context context) {
		dbHelper = new ApplicationDBHelper(context);
	}

	public static AlarmLightColorDAO getDao(Context context) {
		if (dao == null) {
			dao = new AlarmLightColorDAO(context);
		}
		return dao;
	}

	public void saveOrUpdate(AlarmLightColor color) {
		if(exist(color.getAlarm_id() + "")){
			update(color);
		}else{
			insert(color);
		}
	}
	
	private void insert(AlarmLightColor color) {
		ContentValues values = AlarmLightColorBuilder.deconstruct(color);
		getDatabase().insert(TABLE_NAME, null, values);
	}
	
	private void update(AlarmLightColor color) {
		ContentValues values = AlarmLightColorBuilder.deconstruct(color);
		getDatabase().update(TABLE_NAME, values, AlarmLightColorBuilder._ID + "=?", new String[]{color.getId() + ""});
	}

	public void delete(AlarmLightColor color) {
		getDatabase().delete(TABLE_NAME, AlarmLightColorBuilder._ID + "=?", new String[]{color.getId() + ""});
	}
	
	public AlarmLightColor query(String alarmId){
		AlarmLightColor alarm = null;
		Cursor cursor = getDatabase().query(TABLE_NAME,
				null,
				AlarmLightColorBuilder.ALARM_ID + "=?",
				new String[] { alarmId }, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				alarm = AlarmLightColorBuilder.build(cursor);
				break;
			} while (cursor.moveToNext());
		}
		return alarm;
	}

	private boolean exist(String alarmId) {
		Cursor cursor = getDatabase().query(TABLE_NAME,
				new String[] { AlarmLightColorBuilder._ID },
				AlarmLightColorBuilder.ALARM_ID + "=?",
				new String[] { alarmId }, null, null, null);
		return cursor != null && cursor.getCount() > 0;
	}

	public SQLiteDatabase getDatabase() throws SQLiteException {
		if (null == database) {
			database = dbHelper.getWritableDatabase();
		}
		return database;
	}
}
