package com.chipsguide.app.colorbluetoothlamp.v2.utils;

import java.text.NumberFormat;

import android.text.TextUtils;

public class StringFormatUtil {
	
	public static String formatDuration(long milliSecond) {
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		long day = hh * 24;
		long month = day * 30;
		long year = month * 12;

		long years = (milliSecond) / year;
		long months = (long) ((milliSecond - years * year) / month);
		long days = (milliSecond - years * year - months * month) / day;
		long hour = (long) ((milliSecond - years * year - months * month - days
				* day) / hh);
		long minute = (int) ((milliSecond - years * year - months * month
				- days * day - hour * hh) / mi);
		long second = (long) ((milliSecond - years * year - months * month
				- days * day - hour * hh - minute * mi) / ss);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		String result = "";
		if (years != 0) {
			result += years + ":";
		}
		if (months != 0) {
			result += months + ":";
		}
		if (days != 0) {
			result += days + ":";
		}
		if (hour != 0) {
			result += hour + ":";
		}

		if (minute < 10) {
			result += ("0" + minute) + ":";
		} else {
			result += minute + ":";
		}

		if (second < 10) {
			result += ("0" + second);
		} else {
			result += second;
		}

		if (TextUtils.isEmpty(result)) {
			result = "00:00";
		}
		return result;
	}
}
