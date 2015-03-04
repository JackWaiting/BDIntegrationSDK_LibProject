package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.connect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectDBHelper extends SQLiteOpenHelper
{
	private final static String NAME = "connect.db";
	private final static int VERSION = 1;
	public final static String TABLE_CONNECT_BLUETOOTH = "connect";
	public ConnectDBHelper(Context context)
	{
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		// TODO onCreate
		db.execSQL(getConnectSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO onUpgrade
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECT_BLUETOOTH);
	}
	
	private String getConnectSql()
	{
		String sql = "create table " + TABLE_CONNECT_BLUETOOTH + "(" + Connect._ID
				+ " integer PRIMARY KEY AUTOINCREMENT," + Connect.ID
				+ " text, " + Connect.NAME + " text," + Connect.CONTENT_NAME
				+ " text," + Connect.MAC_ADDRESS + " text," + Connect.ISCONNECT + " text," 
				+ Connect.DATE + " text,"+ Connect.DEVICE + " text)";
		return sql;
	}

	
	public final static class Connect
	{
		private Connect(){}

		public final static String _ID = "_id";// id自增长
		public final static String ID = "id";//蓝牙id
		public final static String NAME = "name";//蓝牙名称
		public final static String CONTENT_NAME = "content_name";//蓝牙备注名称
		public final static String MAC_ADDRESS = "mac_address";//蓝牙Max地址
		public final static String ISCONNECT = "isconnect";// 是否连接：1连接、0不连接
		public final static String DATE = "date";// 最后连接日期
		public final static String DEVICE = "device";

	}

}
