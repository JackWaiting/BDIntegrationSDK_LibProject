package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils;

/**   
 * @author 作者 :   王贵重
 * @version 创建时间：2014年5月22日 下午7:22:18  
 * 类说明  时间格式化
 */
public class FormatHelper {
	/**
	 * return a format time of 00:00
	 * @param milliseconds
	 * @return
	 */
	public static String formatDuration(int milliseconds)
	{
		int seconds = milliseconds / 1000;
		int secondPart = seconds % 60;
		int minutePart = seconds / 60;
		return (minutePart >= 10 ? minutePart : "0" + minutePart) + ":" + (secondPart >= 10 ? secondPart : "0" + secondPart);
	}
	
	public static String formatTitle(String title, int length)
	{
		int len = title.length() < length ? title.length():length;		
		String subString = title.substring(0, len);
		if(len < title.length())
		{
			subString += "...";
		}
		return subString;
	}
	
	public static String formatLongToTimeStr(Long l)
	{
		int hour = 0;
		int minute = 0;
		int second = 0;
		String strhour;

		second = l.intValue() / 1000;

		if (second > 60)
		{
			minute = second / 60;
			second = second % 60;
		}
		if (minute > 60)
		{
			hour = minute / 60;
			minute = minute % 60;
		}
		if(hour > 0)
		{
			strhour = getTwoLength(hour) + ":";
		}else
		{
			strhour = "";
		}
		return ( strhour + getTwoLength(minute) + ":" + getTwoLength(second));
	}
	
	public static String formatLongToTimeMinuteStr(Long l)
	{
		int hour = 0;
		int minute = 0;
		int second = 0;
		String strhour;

		second = l.intValue() / 1000;

		if (second > 60)
		{
			minute = second / 60;
			second = second % 60;
		}
		if(hour > 0)
		{
			strhour = getTwoLength(hour) + ":";
		}else
		{
			strhour = "";
		}
		return ( strhour + getTwoLength(minute) + ":" + getTwoLength(second));
	}

	private static String getTwoLength(final int data)
	{
		if (data < 10)
		{
			return "0" + data;
		}
		else
		{
			return "" + data;
		}
	}
}
