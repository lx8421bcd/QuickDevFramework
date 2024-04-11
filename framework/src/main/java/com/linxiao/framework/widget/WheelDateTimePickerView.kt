package com.linxiao.framework.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.linxiao.framework.R
import com.linxiao.framework.databinding.ViewWheelTimeSelectBinding
import com.linxiao.framework.widget.WheelDateTimePickerView.ItemTextBuilder
import com.linxiao.framework.widget.wheelview.WheelView
import com.linxiao.framework.widget.wheelview.adapter.AbstractWheelTextAdapter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class WheelDateTimePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    enum class WheelDateTimePickerMode {
        dateAndTime,
        date,
        time,
        monthYear,
        dayMonth,
        hourMinute,
        minuteSecond,
        year,
        month,
        day,
        hour,
        minute,
        second
    }

    fun interface OnTimeSelectedListener {
        fun onTimeSelected(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int)
    }

    fun interface ItemTextBuilder {
        fun onBuildItemText(value: Int): String
    }

    private inner class CalendarWheelAdapter(
        private val list: List<Int>,
        private val itemTextBuilder: ItemTextBuilder
    ) : AbstractWheelTextAdapter(context) {

        override fun getItemText(index: Int): CharSequence {
            return itemTextBuilder.onBuildItemText(list[index])
        }

        override fun getItemsCount(): Int {
            return list.size
        }
    }

    private val viewBinding by lazy {
        ViewWheelTimeSelectBinding.bind(this)
    }
    private val years: MutableList<Int> = ArrayList()
    private val months: MutableList<Int> = ArrayList()
    private val days: MutableList<Int> = ArrayList()
    private val hours: MutableList<Int> = ArrayList()
    private val minutes: MutableList<Int> = ArrayList()
    private val seconds: MutableList<Int> = ArrayList()

    var selectedYear = 0
        private set
    var selectedMonth = 0
        private set
    var selectedDay = 0
        private set
    var selectedHour = 0
        private set
    var selectedMinute = 0
        private set
    var selectedSecond = 0
        private set

    var rangeStartDateTime = LocalDateTime.of(1900, 1, 1, 0, 0, 0)
    var rangeEndDateTime = LocalDateTime.of(LocalDateTime.now().year + 100, 1, 1, 0, 0, 0)

    var yearTextBuilder = ItemTextBuilder { value -> value.toString() }
    var monthTextBuilder = ItemTextBuilder { value -> value.toString() }
    var dayTextBuilder = ItemTextBuilder { value -> value.toString() }
    var hourTextBuilder = ItemTextBuilder { value -> value.toString() }
    var minuteTextBuilder = ItemTextBuilder { value -> value.toString() }
    var secondTextBuilder = ItemTextBuilder { value -> value.toString() }

    var onTimeSelectedListener: OnTimeSelectedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_wheel_time_select, this, true)
        setWheelShowStroke(true)
        setWheelVisibleItemCount(5)
        viewBinding.wheelYear.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedYear = years[viewBinding.wheelYear.getCurrentItem()]
            updateMonths()
            updateDaysOfMonth()
            updateHours()
            updateMinutes()
            updateSeconds()
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        viewBinding.wheelMonth.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedMonth = months[viewBinding.wheelMonth.getCurrentItem()]
            updateDaysOfMonth()
            updateHours()
            updateMinutes()
            updateSeconds()
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        viewBinding.wheelDay.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedDay = days[viewBinding.wheelDay.getCurrentItem()]
            updateHours()
            updateMinutes()
            updateSeconds()
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        viewBinding.wheelHour.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedHour = hours[viewBinding.wheelHour.getCurrentItem()]
            updateMinutes()
            updateSeconds()
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        viewBinding.wheelMinute.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedMinute = minutes[viewBinding.wheelMinute.getCurrentItem()]
            updateSeconds()
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        viewBinding.wheelSeconds.addChangingListener { wheel: WheelView, oldValue: Int, newValue: Int ->
            selectedSecond = seconds[viewBinding.wheelSeconds.getCurrentItem()]
            onTimeSelectedListener?.onTimeSelected(
                selectedYear,
                selectedMonth,
                selectedDay,
                selectedHour,
                selectedMinute,
                selectedSecond
            )
        }
        updateYears()
        updateMonths()
        updateDaysOfMonth()
        updateHours()
        updateMinutes()
        updateSeconds()
    }

    fun setMode(mode: WheelDateTimePickerMode) {
        viewBinding.wheelYear.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.date,
            WheelDateTimePickerMode.monthYear,
            WheelDateTimePickerMode.year,
        ).contains(mode)
        viewBinding.wheelMonth.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.date,
            WheelDateTimePickerMode.monthYear,
            WheelDateTimePickerMode.dayMonth,
            WheelDateTimePickerMode.month,
        ).contains(mode)
        viewBinding.wheelDay.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.date,
            WheelDateTimePickerMode.dayMonth,
            WheelDateTimePickerMode.day,
        ).contains(mode)
        viewBinding.wheelHour.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.time,
            WheelDateTimePickerMode.hourMinute,
            WheelDateTimePickerMode.hour,
        ).contains(mode)
        viewBinding.wheelMinute.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.time,
            WheelDateTimePickerMode.hourMinute,
            WheelDateTimePickerMode.minuteSecond,
            WheelDateTimePickerMode.minute,
        ).contains(mode)
        viewBinding.wheelSeconds.isVisible = arrayOf(
            WheelDateTimePickerMode.dateAndTime,
            WheelDateTimePickerMode.time,
            WheelDateTimePickerMode.minuteSecond,
            WheelDateTimePickerMode.second,
        ).contains(mode)
    }

    fun setWheelShowStroke(show: Boolean) {
        viewBinding.wheelYear.showSelectStroke(show)
        viewBinding.wheelMonth.showSelectStroke(show)
        viewBinding.wheelDay.showSelectStroke(show)
        viewBinding.wheelHour.showSelectStroke(show)
        viewBinding.wheelMinute.showSelectStroke(show)
        viewBinding.wheelSeconds.showSelectStroke(show)
    }

    fun setWheelVisibleItemCount(count: Int) {
        viewBinding.wheelYear.visibleItems = count
        viewBinding.wheelMonth.visibleItems = count
        viewBinding.wheelDay.visibleItems = count
        viewBinding.wheelHour.visibleItems = count
        viewBinding.wheelMinute.visibleItems = count
        viewBinding.wheelSeconds.visibleItems = count
    }

    fun setSelected(timeMills: Long) {
        setSelected(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMills), ZoneId.systemDefault()))
    }

    fun setSelected(date: LocalDate) {
        setSelected(LocalDateTime.from(date))
    }

    fun setSelected(time: LocalTime) {
        setSelected(LocalDateTime.from(time))
    }

    fun setSelected(dateTime: LocalDateTime) {
        selectedYear = dateTime.year
        selectedMonth = dateTime.monthValue
        selectedDay = dateTime.dayOfMonth
        selectedHour = dateTime.hour
        selectedMinute = dateTime.minute
        selectedSecond = dateTime.second
        updateYears()
        updateMonths()
        updateDaysOfMonth()
        updateHours()
        updateMinutes()
        updateSeconds()
    }

    fun getSelectedDateTime(): LocalDateTime {
        return LocalDateTime.of(
            selectedYear,
            selectedMonth,
            selectedDay,
            selectedHour,
            selectedMinute,
            selectedSecond
        )
    }

    private fun updateYears() {
        var yearStart = rangeStartDateTime.year
        var yearEnd = rangeEndDateTime.year
        yearStart = min(yearStart, yearEnd)
        yearEnd = max(yearStart, yearEnd)
        years.clear()
        for (year in yearStart..yearEnd) {
            years.add(year)
        }
        val yearCurrent = LocalDateTime.now().year
        if (selectedYear == 0) {
            selectedYear = yearCurrent
        }
        if (!years.contains(selectedYear)) {
            val startToCurrent = abs(yearCurrent - yearStart)
            val endToCurrent = abs(yearCurrent - yearEnd)
            selectedYear = if (startToCurrent < endToCurrent) yearStart else yearEnd
        }
        viewBinding.wheelYear.viewAdapter = CalendarWheelAdapter(years, yearTextBuilder)
        viewBinding.wheelYear.setCurrentItem(years.indexOf(selectedYear))
    }

    private fun updateMonths() {
        if (selectedYear == 0 || years.isEmpty()) {
            updateYears()
        }
        var monthStart = 1
        var monthEnd = 12
        if (selectedYear == years.first()) {
            monthStart = rangeStartDateTime.month.value
        }
        if (selectedYear == years.last()) {
            monthEnd = rangeEndDateTime.month.value
        }
        months.clear()
        for (month in monthStart..monthEnd) {
            months.add(month)
        }
        if (!months.contains(selectedMonth)) {
            selectedMonth = months.first()
        }
        viewBinding.wheelMonth.viewAdapter = CalendarWheelAdapter(months, monthTextBuilder)
        viewBinding.wheelMonth.setCurrentItem(months.indexOf(selectedMonth))
    }

    private fun updateDaysOfMonth() {
        if (selectedMonth == 0 || months.isEmpty()) {
            updateMonths()
        }
        var dayStart = 1
        var dayEnd = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth() // max days of month

        if (selectedYear == years.first() && selectedMonth == months.first()) {
            dayStart = rangeStartDateTime.dayOfMonth
        }
        if (selectedYear == years.last() && selectedMonth == months.last()) {
            dayEnd = rangeEndDateTime.dayOfMonth
        }
        days.clear()
        for (day in dayStart..dayEnd) {
            days.add(day)
        }
        if (days.contains(selectedDay)){
            selectedDay = days.first()
        }
        viewBinding.wheelDay.viewAdapter = CalendarWheelAdapter(days, dayTextBuilder)
        viewBinding.wheelDay.setCurrentItem(days.indexOf(selectedDay))
    }

    private fun updateHours() {
        if (selectedDay == 0 || days.isEmpty()) {
            updateDaysOfMonth()
        }
        var hourStart = 0
        var hourEnd = 23
        if (selectedYear == years.first() &&
            selectedMonth == months.first() &&
            selectedDay == days.first()
        ) {
            hourStart = rangeStartDateTime.hour
        }
        if (selectedYear == years.last() &&
            selectedMonth == months.last() &&
            selectedDay == days.last()
        ) {
            hourEnd = rangeEndDateTime.hour
        }
        hours.clear()
        for (hour in hourStart..hourEnd) {
            hours.add(hour)
        }
        if (!hours.contains(selectedHour)) {
            selectedHour = hours.first()
        }
        viewBinding.wheelHour.viewAdapter = CalendarWheelAdapter(hours, hourTextBuilder)
        viewBinding.wheelHour.setCurrentItem(hours.indexOf(selectedHour))
    }

    private fun updateMinutes() {
        if (hours.isEmpty()) {
            updateHours()
        }
        var minuteStart = 0
        var minuteEnd = 59
        if (selectedYear == years.first() &&
            selectedMonth == months.first() &&
            selectedDay == days.first() &&
            selectedHour == hours.first()
        ) {
            minuteStart = rangeStartDateTime.minute
        }
        if (selectedYear == years.last() &&
            selectedMonth == months.last() &&
            selectedDay == days.last() &&
            selectedHour == hours.last()
        ) {
            minuteEnd = rangeEndDateTime.minute
        }
        minutes.clear()
        for (minute in minuteStart..minuteEnd) {
            minutes.add(minute)
        }
        if (!minutes.contains(selectedMinute)) {
            selectedMinute =  minutes.first()
        }
        viewBinding.wheelMinute.viewAdapter = CalendarWheelAdapter(minutes, minuteTextBuilder)
        viewBinding.wheelMinute.setCurrentItem(minutes.indexOf(selectedMinute))
    }

    private fun updateSeconds() {
        if (minutes.isEmpty()) {
            updateMinutes()
        }
        var secondStart = 0
        var secondEnd = 59
        if (selectedYear == years.first() &&
            selectedMonth == months.first() &&
            selectedDay == days.first() &&
            selectedHour == hours.first() &&
            selectedMinute == minutes.first()
        ) {
            secondStart = rangeStartDateTime.second
        }
        if (selectedYear == years.last() &&
            selectedMonth == months.last() &&
            selectedDay == days.last() &&
            selectedHour == hours.last() &&
            selectedMinute == minutes.last()
        ) {
            secondEnd = rangeEndDateTime.second
        }
        seconds.clear()
        for (second in secondStart..secondEnd) {
            seconds.add(second)
        }
        if (seconds.contains(selectedSecond)) {
            selectedSecond = seconds.first()
        }
        viewBinding.wheelSeconds.viewAdapter = CalendarWheelAdapter(seconds, secondTextBuilder)
        viewBinding.wheelSeconds.setCurrentItem(seconds.indexOf(selectedSecond))
    }
}