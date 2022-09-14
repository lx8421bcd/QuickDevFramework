package com.linxiao.framework.common;

import android.text.TextUtils;

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
 * @since 2014/8/21
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
	public static final String MILLISECOND = "SSS";

	/*常用时间单位毫秒数，到天，再往上没有意义了，请通过日历获取*/
	/**一秒的毫秒数*/
	public static final long MS_ONE_SECOND = 1000;
	/**一分钟的毫秒数*/
	public static final long MS_ONE_MINUTE = MS_ONE_SECOND * 60;
	/**一小时的毫秒数*/
	public static final long MS_ONE_HOUR = MS_ONE_MINUTE * 60;
	/**一天的毫秒数*/
	public static final long MS_ONE_DAY = MS_ONE_HOUR * 24;
	/**一分钟的秒数*/
	public static final long SEC_ONE_MINUTE = MS_ONE_MINUTE / MS_ONE_SECOND;
	/**一小时的秒数*/
	public static final long SEC_ONE_HOUR = MS_ONE_HOUR / MS_ONE_SECOND;
	/**一天的秒数*/
	public static final long SEC_ONE_DAY = MS_ONE_DAY / MS_ONE_SECOND;

	/**
	 * 获取今日0点时间戳，单位毫秒
	 * @return timestamp in milliseconds
	 */
	public static long getZeroTimeMillsOfToday() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取今日0点时间戳，单位秒
	 * @return timestamp in milliseconds
	 */
	public static long getZeroTimeSecOfToday() {
		return getZeroTimeMillsOfToday() / 1000;
	}

	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static long currentTimeSec() {
		return System.currentTimeMillis() / 1000;
	}

	public static String currentTimeString(String format) {
		return timeMillsToDateString(format, currentTimeMillis());
	}

	public static Date currentDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getDefault());
		return calendar.getTime();
	}

	public static long getPastTimeMillsToday() {
		return currentTimeMillis() - getZeroTimeMillsOfToday();
	}

	public static long getPastTimeSecToday() {
		return getPastTimeMillsToday() / 1000;
	}

	/**
	 * convert date string to timestamp
	 *
	 * @param timeZone timezone, default using current timezone
	 * @param format date string format
	 * @param dateString date string
	 * @return timestamp in milliseconds
	 */
	public static long dateStringToTimeMills(String timeZone, String format, String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		if (!TextUtils.isEmpty(timeZone)) {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		}
		try {
			Date date = sdf.parse(dateString);
			return date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * convert date string to timestamp
	 *
	 * @param format date string format
	 * @param dateString date string
	 * @return date string: timestamp in milliseconds; time string: past milliseconds from today
	 */
	public static long dateStringToTimeMills(String format, String dateString) {
		// 日期格式的时间默认使用当前时区返回时间戳
		if (format.contains(YEAR) || format.contains(MONTH) || format.contains(DAY) || format.contains(WEEK)) {
			return dateStringToTimeMills(null, format, dateString);
		}
		return dateStringToTimeMills("GMT", format, dateString);
	}

	/**
	 * convert date string to timestamp
	 *
	 * @param format date string format
	 * @param dateString date string
	 * @return date string: timestamp in seconds; time string: past seconds from today
	 */
	public static long dateStringToTimeSec(String format, String dateString) {
		return dateStringToTimeMills(format, dateString) / 1000;
	}

	/**
	 * convert timestamp in milliseconds to string
	 *
	 * @param format date string format
	 * @param timeMills date object
	 * @return date string
	 */
	public static String timeMillsToDateString(String format, long timeMills) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		try {
			return sdf.format(new Date(timeMills));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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

	public static boolean isInThePeriod(String format, long start, String end) {
		return isInThePeriod(
				start,
				dateStringToTimeMills(format, end)
		);
	}

	public static boolean isInThePeriod(String format, String start, long end) {
		return isInThePeriod(
				dateStringToTimeMills(format, start),
				end
		);
	}
	public static boolean isInThePeriod(String format, String start, String end) {
		return isInThePeriod(
				dateStringToTimeMills(format, start),
				dateStringToTimeMills(format, end)
		);
	}

}
