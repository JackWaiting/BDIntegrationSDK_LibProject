package com.chipsguide.app.colorbluetoothlamp.v2.connect;

import java.util.ArrayList;
import java.util.Date;

import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chipsguide.app.colorbluetoothlamp.v2.connect.ConnectDBHelper.Connect;
import com.google.gson.Gson;

public class ConnectDao
{
	final static String TAG = "ConnectDao";
	private ConnectDBHelper helper;
	private static ConnectDao dao = null;
	private Context mContext;
	private DBDao dbDao;

	public ConnectDao(Context context)
	{
		this.mContext = context;
		dbDao = DBDao.getInstance(mContext);
		this.helper = new ConnectDBHelper(context);
	}

	/**
	 * 获取数据库操作实例
	 * 
	 * @param context
	 *            Context
	 * @return ConnectDao
	 */
	public static synchronized ConnectDao getDao(Context context)
	{
		if (null == dao)
		{
			dao = new ConnectDao(context);
		}
		return dao;
	}

	/**
	 * 查询某个蓝牙是否为连接
	 * 
	 * @param id
	 * @return 存在返回true，否则返回false
	 */
	public synchronized boolean isConnect(String id)
	{
		boolean flag = false;
		Cursor cursor = null;
		try
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = "select * from "
					+ ConnectDBHelper.TABLE_CONNECT_BLUETOOTH + " where "
					+ Connect.ID + " = " + id;
			cursor = db.rawQuery(sql, null);
			Log.i(TAG, "sql: " + sql);
			ConnectInfo info = null;
			if (null != cursor)
			{
				cursor.moveToFirst();
				if (cursor.getCount() > 0)
				{
					info = getInfo(cursor);
					Log.i(TAG, "info:" + info.toString());
					if (info.getIsconnect().equals("1"))
					{
						flag = true;
					}
				}
			}

		} catch (Exception e)
		{
			// TODO: handle exception
		} finally
		{
			if (null != cursor)
			{
				cursor.close();
				cursor = null;
			}
		}
		return flag;
	}

	public synchronized void updateIsConnect(String id, String isconnect)
	{
		try
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			String where = Connect.ID + " = ?";
			String[] whereArgs = { id };
			ContentValues values = new ContentValues();
			values.put(Connect.ISCONNECT, isconnect);
			db.update(ConnectDBHelper.TABLE_CONNECT_BLUETOOTH, values, where,
					whereArgs);
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public synchronized void updateChangeName(String macaddress, String name)
	{
		try
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = "update " + ConnectDBHelper.TABLE_CONNECT_BLUETOOTH
					+ " set " + Connect.CONTENT_NAME + " = '" + name +  "'  where " + Connect.MAC_ADDRESS + " = '" + macaddress + "'";
			db.execSQL(sql);
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	public synchronized void updateChangeData(String id, String date)
	{
		try
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			String sql = "update " + ConnectDBHelper.TABLE_CONNECT_BLUETOOTH
					+ " set " + Connect.DATE + " = '" + date +  "'  where " + Connect.ID + " = '" + id + "'";
			db.execSQL(sql);
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}


	/**
	 * 查询蓝牙信息
	 * 
	 * @param id
	 *            应用id
	 * @return ConnectInfo
	 */
	public synchronized ConnectInfo select(String id)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql;
		Cursor cursor = null;
		ConnectInfo info = null;
		try
		{
			sql = "select * from " + ConnectDBHelper.TABLE_CONNECT_BLUETOOTH
					+ " where " + Connect.ID + " = " + id;
			cursor = db.rawQuery(sql, null);

			if (null != cursor)
			{
				cursor.moveToFirst();
				if (cursor.getCount() > 0)
				{
					info = getInfo(cursor);
				}
			}
		} catch (Exception e)
		{
			// TODO: handle exception
		} finally
		{

			if (null != cursor)
			{
				cursor.close();
				cursor = null;
			}
		}

		return info;
	}

	/**
	 * 查询所有蓝牙信息
	 * @return List
	 */
	public synchronized ArrayList<ConnectInfo> selectAll()
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		ArrayList<ConnectInfo> list = new ArrayList<ConnectInfo>();
		Cursor cursor = null;
		try
		{
			db.beginTransaction();
			String sql = "select * from "
					+ ConnectDBHelper.TABLE_CONNECT_BLUETOOTH + " order by "
					+ Connect.DATE + " desc";
			cursor = db.rawQuery(sql, null);

			if (null != cursor)
			{
				cursor.moveToFirst();
				for (int i = 0, size = cursor.getCount(); i < size; i++)
				{
					ConnectInfo info = getInfo(cursor);
					list.add(info);
					cursor.moveToNext();
				}
			}
			db.endTransaction();
		} catch (Exception e)
		{
			// TODO: handle exception
		} finally
		{
			if (null != cursor)
			{
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}

	/**
	 * 是否存在蓝牙信息
	 * 
	 * @param id
	 * @return 存在返回true，否则返回false
	 */
	public synchronized boolean exist(String mac_address)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		String sql;
		Cursor cursor = null;
		boolean flag = false;
		try
		{
			sql = "select " + Connect.MAC_ADDRESS + "," + Connect.ISCONNECT
					+ " from " + ConnectDBHelper.TABLE_CONNECT_BLUETOOTH
					+ " where " + Connect.MAC_ADDRESS + " = '" + mac_address+"'";
			cursor = db.rawQuery(sql, null);
			flag = (null != cursor && cursor.getCount() > 0);

		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		} finally
		{
			if (null != cursor)
			{
				cursor.close();
				cursor = null;
			}
		}

		return flag;
	}

	/**
	 * 增加一条蓝牙信息
	 * 
	 * @param info
	 *            ConnectInfo
	 */
	public synchronized void insert(ConnectInfo info)
	{
		Log.i(TAG, "insert id:  " + info.toString());
		try
		{
			if (!exist(info.getMac_address()))
			{
				SQLiteDatabase db = helper.getReadableDatabase();
				String sql = "insert into "
						+ ConnectDBHelper.TABLE_CONNECT_BLUETOOTH + " ("
						+ Connect.ID + "," + Connect.NAME + ","
						+ Connect.CONTENT_NAME + "," + Connect.MAC_ADDRESS
						+ "," + Connect.ISCONNECT + "," + Connect.DATE + ","
						+ Connect.DEVICE
						+ ") values (?,?,?,?,?,?,?)";
				Object[] bindArgs = { info.getId(), info.getName(), info.getContent_name(),
						info.getMac_address(), info.getIsconnect(),info.getDate(), info.getDevice()};
				db.execSQL(sql, bindArgs);
			}else
			{
				updateChangeData(info.getId(),new Date().toString());
			}
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}
	
	/**
	 * 增加一条蓝牙信息
	 * 
	 * @param info
	 *            ConnectInfo
	 */
	public synchronized void insert(BluetoothDevice bluetoothDevice)
	{
		Log.i(TAG, "insert id:  " + bluetoothDevice);
		try
		{
			if (!exist(bluetoothDevice.getAddress()))
			{
				SQLiteDatabase db = helper.getReadableDatabase();
				String sql = "insert into "
						+ ConnectDBHelper.TABLE_CONNECT_BLUETOOTH + " ("
						+ Connect.ID + "," + Connect.NAME + ","
						+ Connect.CONTENT_NAME + "," + Connect.MAC_ADDRESS
						+ "," + Connect.ISCONNECT + "," + Connect.DATE + ","
						+ Connect.DEVICE
						+ ") values (?,?,?,?,?,?,?)";
				Object[] bindArgs = { bluetoothDevice.getName(), bluetoothDevice.getName(), bluetoothDevice.getName(),
						bluetoothDevice.getAddress(), "0",System.currentTimeMillis(), new Gson().toJson(bluetoothDevice)};
				db.execSQL(sql, bindArgs);
			}else
			{
				updateChangeData(bluetoothDevice.getName(),new Date().toString());
			}
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
	}

	public synchronized boolean delete(String address)
	{
		String sql = "delete from " + ConnectDBHelper.TABLE_CONNECT_BLUETOOTH
				+ " where " + Connect.MAC_ADDRESS + " = '" + address + "'";
		boolean flag = dbDao.delData(sql);
		return flag;
	}

	private ConnectInfo getInfo(Cursor cursor)
	{
		String id = cursor.getString(cursor.getColumnIndex(Connect.ID));
		String name = cursor.getString(cursor.getColumnIndex(Connect.NAME));
		String content_name = cursor.getString(cursor
				.getColumnIndex(Connect.CONTENT_NAME));
		String mac_address = cursor.getString(cursor
				.getColumnIndex(Connect.MAC_ADDRESS));
		String isconnect = cursor.getString(cursor
				.getColumnIndex(Connect.ISCONNECT));
		String device = cursor.getString(cursor.getColumnIndex(Connect.DEVICE));
		long date = cursor.getLong(cursor.getColumnIndex(Connect.DATE));
		ConnectInfo info = new ConnectInfo(id,name,content_name,mac_address,isconnect,date,device);
		return info;
	}

	/**
	 * 关闭数据库
	 */
	public synchronized void close()
	{
		if (null != helper)
		{
			helper.close();
		}
	}
}
