package com.linxiao.framework.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * date tools
 *
 * @author linxiao
 * Create on 2014/8/21
 */
public class DateUtil {
	private static final String TAG = DateUtil.class.getSimpleName();

	public static final String YEAR = "yyyy";
	public static final String MONTH = "MM";
	public static final String DAY = "dd";
	public static final String WEEK = "EEE";
	public static final String HOUR_FORMAT_24 = "HH";
	public static final String HOUR_FORMAT_12 = "hh";
	public static final String MINUTE = "mm";
	public static final String SECOND = "ss";
	
	/*常用时间单位毫秒数，到天，再往上没有意义了，请通过日历获取*/
	/**一秒的毫秒数*/
	public static final long MS_ONE_SECOND = 1000;
	/**一分钟的毫秒数*/
	public static final long MS_ONE_MINUTE = MS_ONE_SECOND * 60;
	/**一小时的毫秒数*/
	public static final long MS_ONE_HOUR = MS_ONE_MINUTE * 60;
	/**一天的毫秒数*/
	public static final long MS_ONE_DAY = MS_ONE_HOUR * 24;

	public static long getZeroTimeOfToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * get current timestamp as millisecond
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	/**
	 * get current timestamp as second
	 */
	public static long currentTimeSecond() {
		return System.currentTimeMillis() / 1000;
	}
	
	/**
	 * get current time as {@link Date} object
	 */
	public static Date currentDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		return calendar.getTime();
	}
	
	/**
	 * convert date string to {@link Date} object
	 *
	 * @param format date string format
	 * @param strDate date string
	 * @return {@link Date} object
	 */
	public static Date convertToDate(String format, String strDate) {
		SimpleDateFormat sdf =  new SimpleDateFormat(format, Locale.getDefault());
		try {
			return sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * convert {@link Date} object to timestamp
	 *
	 * @param date {@link Date} object
	 * @return timestamp
	 */
	public static long convertToTimestamp(Date date) {
		if (date == null) {
			return 0;
		}
		return date.getTime();
	}

	/**
	 * convert date string to timestamp
	 *
	 * @param format date string format
	 * @param strDate date string
	 * @return timestamp
	 */
	public static long convertToTimestamp(String format, String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		try {
			Date date = sdf.parse(strDate);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * convert {@link Date} object to string
	 *
	 * @param format date string format
	 * @param date date object
	 * @return date string
	 */
	public static String formatDate(String format, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		try {
			return sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * convert timestamp object to string
	 *
	 * @param format date string format
	 * @param timestamp date object
	 * @return date string
	 */
	public static String formatDate(String format, long timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		try {
			return sdf.format(new Date(timestamp));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * check if the current time is within the defined time period
	 * 当前是否在指定时间段内
	 *
	 * @param format 格式化字符串
	 * @param start	开始时间戳
	 * @param end	结束时间戳
	 * @return true 在时间段内， false 不在时间段内
	 */
	public static boolean isInThePeriod(String format, long start, String end) {
		return isInThePeriod(
				start,
				convertToTimestamp(format, end)
		);
	}

	/**
	 * check if the current time is within the defined time period
	 * 当前是否在指定时间段内
	 *
	 * @param format 格式化字符串
	 * @param start	开始时间戳
	 * @param end	结束时间戳
	 * @return true 在时间段内， false 不在时间段内
	 */
	public static boolean isInThePeriod(String format, String start, long end) {
		return isInThePeriod(
				convertToTimestamp(format, start),
				end
		);
	}
	/**
	 * check if the current time is within the defined time period
	 * 当前是否在指定时间段内
	 *
	 * @param format 格式化字符串
	 * @param start	开始时间
	 * @param end	结束时间
	 * @return true 在时间段内， false 不在时间段内
	 */
	public static boolean isInThePeriod(String format, String start, String end) {
		return isInThePeriod(
				convertToTimestamp(format, start),
				convertToTimestamp(format, end)
		);
	}

	/**
	 * check if the current time is within the defined time period
	 * 当前是否在指定时间段内
	 *
	 * @param start	开始时间戳
	 * @param end	结束时间戳
	 * @return true 在时间段内， false 不在时间段内
	 */
	public static boolean isInThePeriod(long start, long end) {
		long cur = currentTimeMillis();
		return start <= cur && cur <= end;
	}
}
