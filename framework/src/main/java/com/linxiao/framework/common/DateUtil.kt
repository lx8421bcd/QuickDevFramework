package com.linxiao.framework.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val YEAR = "yyyy"
const val MONTH = "MM"
const val DAY = "dd"
const val WEEK = "EEE"
const val HOUR_FORMAT_24 = "HH"
const val HOUR_FORMAT_12 = "hh"
const val MINUTE = "mm"
const val SECOND = "ss"
const val MILLISECOND = "SSS"

/*常用时间单位毫秒数，到天，再往上没有意义了，请通过日历获取*/
/**一秒的毫秒数 */
const val MS_ONE_SECOND: Long = 1000

/**一分钟的毫秒数 */
const val MS_ONE_MINUTE = MS_ONE_SECOND * 60

/**一小时的毫秒数 */
const val MS_ONE_HOUR = MS_ONE_MINUTE * 60

/**一天的毫秒数 */
const val MS_ONE_DAY = MS_ONE_HOUR * 24

/**一分钟的秒数 */
const val SEC_ONE_MINUTE = MS_ONE_MINUTE / MS_ONE_SECOND

/**一小时的秒数 */
const val SEC_ONE_HOUR = MS_ONE_HOUR / MS_ONE_SECOND

/**一天的秒数 */
const val SEC_ONE_DAY = MS_ONE_DAY / MS_ONE_SECOND

/**
 * date tools
 *
 * @author linxiao
 * @since 2014/8/21
 */
object DateUtil {
    private val TAG = DateUtil::class.java.simpleName

    /**
     * 获取今日0点时间戳，单位毫秒
     * @return timestamp in milliseconds
     */
    val zeroTimeMillisOfToday: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            return calendar.timeInMillis
        }

    /**
     * 获取今日0点时间戳，单位秒
     * @return timestamp in milliseconds
     */
    val zeroTimeSecOfToday: Long
        get() = zeroTimeMillisOfToday / 1000

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    fun currentTimeSec(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun currentTimeString(format: String?): String? {
        return timeMillsToDateString(format, currentTimeMillis())
    }

    fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        return calendar.time
    }

    val pastTimeMillisToday: Long
        get() = currentTimeMillis() - zeroTimeMillisOfToday

    val pastTimeSecToday: Long
        get() = pastTimeMillisToday / 1000

    /**
     * 检查格式化字符串是否包含日期
     */
    fun isFormatContainDate(format: String): Boolean {
        val pattern = SimpleDateFormat(format, Locale.getDefault()).toPattern().lowercase()
        return pattern.contains("y") || pattern.contains("m") || pattern.contains("d")
    }

    /**
     * convert date string to timestamp
     *
     * @param timeZone timezone, default using current timezone
     * @param format date string format
     * @param dateString date string
     * @return timestamp in milliseconds
     */
    fun dateStringToTimeMillis(
        dateString: String,
        format: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: String = "",
    ): Long {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        var setTimeZone = timeZone
        if (setTimeZone.isEmpty() && !isFormatContainDate(format)) {
            setTimeZone = "GMT"
        }
        // 不包含日期的时间默认使用当前时区返回时间戳
        if (setTimeZone.isNotEmpty()) {
            sdf.timeZone = TimeZone.getTimeZone(setTimeZone)
        }
        try {
            val date = sdf.parse(dateString)
            return date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    /**
     * convert date string to timestamp
     *
     * @param format date string format
     * @param dateString date string
     * @return date string: timestamp in seconds; time string: past seconds from today
     */
    fun dateStringToTimeSec(
        dateString: String,
        format: String = "yyyy-MM-dd HH:mm:ss",
        timeZone: String = "",
    ): Long {
        return dateStringToTimeMillis(dateString, format, timeZone) / 1000
    }

    /**
     * convert timestamp in milliseconds to string
     *
     * @param format date string format
     * @param timeMills date object
     * @return date string
     */
    @JvmStatic
    fun timeMillsToDateString(format: String?, timeMills: Long): String? {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return try {
            sdf.format(Date(timeMills))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * check if the current time is within the defined time period
     * 当前是否在指定时间段内
     *
     * @param start    开始时间戳
     * @param end    结束时间戳
     * @return true 在时间段内， false 不在时间段内
     */
    fun isInPeriod(start: Long, end: Long): Boolean {
        val cur = currentTimeMillis()
        return cur in start..end
    }

    fun isInPeriod(format: String, start: String, end: String): Boolean {
        return isInPeriod(
            dateStringToTimeMillis(start, format),
            dateStringToTimeMillis(end, format)
        )
    }
}
