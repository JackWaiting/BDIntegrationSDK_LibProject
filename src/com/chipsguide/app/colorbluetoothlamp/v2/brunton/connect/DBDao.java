package com.chipsguide.app.colorbluetoothlamp.v2.brunton.connect;

import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

/**
 * 数据操作类封装
 */
public class DBDao implements Serializable
{
	private ConnectDBHelper dbHelper = null;
	private static DBDao instance;
	private static final long serialVersionUID = 1L;

	/**
	 * 构造函数
	 * */
	private DBDao(Context context)
	{
		dbHelper = new ConnectDBHelper(context);
		dbHelper.getReadableDatabase();
	}

	/**********************************************************************
	 * 非标准SQL语句的数据库操作 ,sqlite3 语句
	 * */

	/**
	 * 保证数据库单列
	 * */
	public synchronized static DBDao getInstance(Context context)
	{
		if (instance == null)
			instance = new DBDao(context);
		return instance;
	}

	/**
	 * 插入数据
	 * */
	public boolean insertData(String table, String[] colum, String[] values)
	{
		ContentValues cv = new ContentValues();
		long rowId = -1;
		try
		{
			rowId = dbHelper.getReadableDatabase().insert(table, null, cv);
		} catch (Exception e)
		{
			rowId = -1;
			e.printStackTrace();
		}
		if (rowId != -1)
			return true;
		return false;
	}

	/**
	 * 删除数据
	 * */
	public boolean delData(String table, String whereClause, String whereArgs)
	{
		int rows = 0;
		try
		{
			rows = dbHelper.getReadableDatabase().delete(table, whereClause,
					new String[] { whereArgs });
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (rows > 0)
			return true;
		return false;
	}

	/**
	 * 查询数据
	 * */
	public Cursor queryData(String table, String[] columns, String selection,
			String[] selectionArgs)
	{
		Cursor cr = null;
		try
		{
			cr = dbHelper.getReadableDatabase().query(table, columns,
					selection, selectionArgs, null, null, null);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return cr;
	}

	/**
	 * 修改数据
	 * */
	public boolean updateData(String table, String values, String whereClause,
			String[] whereArgs)
	{
		ContentValues cv = new ContentValues();
		int rows = 0;
		cv.put("", values);
		try
		{
			rows = dbHelper.getReadableDatabase().update(table, cv,
					whereClause, whereArgs);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		if (rows > 0)
			return true;
		return false;
	}

	/*********************************************************************
	 * 用SQL语句实现数据库的操作
	 * */

	/**
	 * 插入数据
	 * */
	public boolean insertData(String sql)
	{
		try
		{
			dbHelper.getReadableDatabase().execSQL(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 插入数据
	 * */
	public boolean insertData(String sql, Object[] bindArgs)
	{
		try
		{
			dbHelper.getReadableDatabase().execSQL(sql, bindArgs);
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除数据
	 * */
	public boolean delData(String sql)
	{
		try
		{
			dbHelper.getReadableDatabase().execSQL(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 查询数据
	 * */
	public Cursor queryData(String sql, String[] args)
	{
		Cursor cr = null;
		try
		{
			cr = dbHelper.getReadableDatabase().rawQuery(sql, args);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		return cr;
	}

	/**
	 * 更新数据
	 * */
	public boolean updateData(String sql)
	{
		try
		{
			dbHelper.getReadableDatabase().execSQL(sql);
		} catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 关闭数据库
	 * */
	public void closeDB()
	{
		if (dbHelper.getReadableDatabase() != null)
			dbHelper.close();
	}

	/**
	 * 手动开启事务
	 * */
	public void beginTransaction()
	{
		dbHelper.getReadableDatabase().beginTransaction();
	}

	/**
	 * 手动关闭事务
	 * */
	public void endTransaction()
	{
		dbHelper.getReadableDatabase().endTransaction();
	}

	/**
	 * 设置事务处理成功，若不设置： 会自动回滚，不提交数据
	 * */
	public void setTransactionSuccessful()
	{
		dbHelper.getReadableDatabase().setTransactionSuccessful();
	}

}
