package com.chipsguide.app.colorbluetoothlamp.v2.brunton.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.AlarmLightColor;


public class AlarmLightColorBuilder {
	public static final String _ID = "_id";
	public static final String ALARM_ID = "alarm_id";
	public static final String COLOR = "color";
	
	public static ContentValues deconstruct(AlarmLightColor color) {
		ContentValues cv = new ContentValues(2);
		cv.put(ALARM_ID, color.getAlarm_id());
		cv.put(COLOR, color.getColor());
		return cv;
	}
	
	public static AlarmLightColor build(Cursor cursor) {
		AlarmLightColor color = new AlarmLightColor();
		color.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
		color.setAlarm_id(cursor.getInt(cursor.getColumnIndex(ALARM_ID)));
		color.setColor(cursor.getString(cursor.getColumnIndex(COLOR)));
		return color;
	}
	
	public static String getAlarmLightColorSql() {
		return "CREATE TABLE IF NOT EXISTS " + AlarmLightColorDAO.TABLE_NAME + " ( "
				+ _ID + " INTEGER primary key autoincrement, "
				+ ALARM_ID + " INTEGER,"
				+ COLOR + " TEXT"
				+ ")";
	}
	
}
