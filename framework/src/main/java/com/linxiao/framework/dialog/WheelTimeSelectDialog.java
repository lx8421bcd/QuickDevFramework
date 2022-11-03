package com.linxiao.framework.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.linxiao.framework.R;
import com.linxiao.framework.databinding.DialogWheelTimeSelectBinding;
import com.linxiao.framework.widget.wheelview.adapter.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class WheelTimeSelectDialog extends Dialog {

    public interface OnTimeSelectedListener {

        void onTimeSelected(int year, int month, int day, int hour, int minute, int second);

    }

    private DialogWheelTimeSelectBinding viewBinding;
    
    private final List<Integer> years = new ArrayList<>();
    private final List<Integer> months = new ArrayList<>();
    private final List<Integer> days = new ArrayList<>();
    private final List<Integer> hours = new ArrayList<>();
    private final List<Integer> minutes = new ArrayList<>();
    private final List<Integer> seconds = new ArrayList<>();

    private int yearEnd = Calendar.getInstance().get(Calendar.YEAR);
    private int yearStart = Calendar.getInstance().get(Calendar.YEAR);
    private boolean showYear = true;
    private boolean showMonth = true;
    private boolean showDay = true;
    private boolean showHour = true;
    private boolean showMinute = true;
    private boolean showSecond = true;

    private int selectedYear = 0;
    private int selectedMonth = 1;
    private int selectedDay = 1;
    private int selectedHour = 0;
    private int selectedMinute = 0;
    private int selectedSecond = 0;

    private OnTimeSelectedListener onTimeSelectedListener;

    public WheelTimeSelectDialog(Context context) {
        super(context);
    }

    private WheelTimeSelectDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private WheelTimeSelectDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public WheelTimeSelectDialog setYearRange(int startYear, int endYear) {
        if (startYear != 0) {
            this.yearStart = startYear;
        }
        if (endYear != 0) {
            this.yearEnd = endYear;
        }
        return this;
    }

    public WheelTimeSelectDialog setShowYear(boolean showYear) {
        this.showYear = showYear;
        return this;
    }

    public WheelTimeSelectDialog setShowMonth(boolean showMonth) {
        this.showMonth = showMonth;
        return this;
    }

    public WheelTimeSelectDialog setShowDay(boolean showDay) {
        this.showDay = showDay;
        return this;
    }

    public WheelTimeSelectDialog setShowHour(boolean showHour) {
        this.showHour = showHour;
        return this;
    }

    public WheelTimeSelectDialog setShowMinute(boolean showMinute) {
        this.showMinute = showMinute;
        return this;
    }

    public WheelTimeSelectDialog setShowSecond(boolean showSecond) {
        this.showSecond = showSecond;
        return this;
    }

    public WheelTimeSelectDialog setOnTimeSelectedListener(OnTimeSelectedListener onTimeSelectedListener) {
        this.onTimeSelectedListener = onTimeSelectedListener;
        return this;
    }

    public WheelTimeSelectDialog setSelected(long timeMills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMills);
        return setSelected(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
    }

    public WheelTimeSelectDialog setSelected(int year, int month, int day, int hour, int minute, int second) {
        selectedYear = year > yearEnd || year < yearStart ? selectedYear : year;
        selectedMonth = month > 12 || month < 1 ? selectedMonth : month;
        Calendar calendar = new GregorianCalendar(selectedYear, selectedMonth, 1);
        int dayOfMonCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        selectedDay = day > dayOfMonCount || day < 1 ? selectedDay : day;
        selectedHour = hour > 23 || hour < 0 ? selectedHour : hour;
        selectedMinute = minute > 59 || minute < 0 ? selectedMinute : minute;
        selectedSecond = second > 59 || second < 0 ? selectedSecond : second;
        return this;
    }

    private void initCalendarData() {
        for (int i = Math.min(yearStart, yearEnd); i <= Math.max(yearStart, yearEnd); i++) {
            years.add(i);
        }
        if (yearStart < Calendar.getInstance().get(Calendar.YEAR)) {
            Collections.reverse(years);
        }
        if (selectedYear == 0) {
            selectedYear = years.get(0);
        }
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        Calendar calendar = new GregorianCalendar(yearStart, 1, 1);
        int dayOfMonCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= dayOfMonCount; i++) {
            days.add(i);
        }
        for (int i = 0; i < 24; i++) {
            hours.add(i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i);
        }
        for (int i = 0; i < 60; i++) {
            seconds.add(i);
        }

        if (viewBinding != null) {
            viewBinding.wheelYear.setCurrentItem(years.indexOf(selectedYear));
            viewBinding.wheelMonth.setCurrentItem(months.indexOf(selectedMonth));
            viewBinding.wheelDay.setCurrentItem(days.indexOf(selectedDay));
            viewBinding.wheelHour.setCurrentItem(hours.indexOf(selectedHour));
            viewBinding.wheelMinute.setCurrentItem(minutes.indexOf(selectedMinute));
            viewBinding.wheelSeconds.setCurrentItem(seconds.indexOf(selectedSecond));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wheel_time_select);
        viewBinding = DialogWheelTimeSelectBinding.bind(findViewById(R.id.root_view));
        viewBinding.tvConfirm.setOnClickListener(v -> selectTime());
        viewBinding.tvCancel.setOnClickListener(v -> dismiss());

        initCalendarData();

        viewBinding.wheelYear.showSelectStroke(true);
        viewBinding.wheelMonth.showSelectStroke(true);
        viewBinding.wheelDay.showSelectStroke(true);
        viewBinding.wheelHour.showSelectStroke(true);
        viewBinding.wheelMinute.showSelectStroke(true);
        viewBinding.wheelSeconds.showSelectStroke(true);

        viewBinding.wheelYear.setVisibleItems(5);
        viewBinding.wheelMonth.setVisibleItems(5);
        viewBinding.wheelDay.setVisibleItems(5);
        viewBinding.wheelHour.setVisibleItems(5);
        viewBinding.wheelMinute.setVisibleItems(5);
        viewBinding.wheelSeconds.setVisibleItems(5);

        viewBinding.wheelYear.setViewAdapter(new CalendarWheelAdapter(years, "年"));
        viewBinding.wheelMonth.setViewAdapter(new CalendarWheelAdapter(months, "月"));
        viewBinding.wheelDay.setViewAdapter(new CalendarWheelAdapter(days, "日"));
        viewBinding.wheelHour.setViewAdapter(new CalendarWheelAdapter(hours));
        viewBinding.wheelMinute.setViewAdapter(new CalendarWheelAdapter(minutes));
        viewBinding.wheelSeconds.setViewAdapter(new CalendarWheelAdapter(seconds));

        viewBinding.wheelYear.addChangingListener((wheel, oldValue, newValue) -> {
            selectedYear = years.get(viewBinding.wheelYear.getCurrentItem());
            updateDayOfMonth();
        });
        viewBinding.wheelMonth.addChangingListener((wheel, oldValue, newValue) -> {
            selectedMonth = months.get(viewBinding.wheelMonth.getCurrentItem());
            updateDayOfMonth();
        });
        viewBinding.wheelDay.addChangingListener((wheel, oldValue, newValue) -> {
            selectedDay = days.get(viewBinding.wheelDay.getCurrentItem());
        });
        viewBinding.wheelHour.addChangingListener((wheel, oldValue, newValue) -> {
            selectedHour = hours.get(viewBinding.wheelHour.getCurrentItem());
        });
        viewBinding.wheelMinute.addChangingListener((wheel, oldValue, newValue) -> {
            selectedMinute = minutes.get(viewBinding.wheelMinute.getCurrentItem());
        });
        viewBinding.wheelSeconds.addChangingListener((wheel, oldValue, newValue) -> {
            selectedSecond = seconds.get(viewBinding.wheelSeconds.getCurrentItem());
        });
        viewBinding.wheelYear.setVisibility(showYear ? View.VISIBLE : View.GONE);
        viewBinding.wheelMonth.setVisibility(showMonth ? View.VISIBLE : View.GONE);
        viewBinding.wheelDay.setVisibility(showDay ? View.VISIBLE : View.GONE);
        viewBinding.wheelHour.setVisibility(showHour ? View.VISIBLE : View.GONE);
        viewBinding.wheelMinute.setVisibility(showMinute ? View.VISIBLE : View.GONE);
        viewBinding.wheelSeconds.setVisibility(showSecond ? View.VISIBLE : View.GONE);

        viewBinding.wheelYear.setCurrentItem(years.indexOf(selectedYear));
        viewBinding.wheelMonth.setCurrentItem(months.indexOf(selectedMonth));
        viewBinding.wheelDay.setCurrentItem(days.indexOf(selectedDay));
        viewBinding.wheelHour.setCurrentItem(hours.indexOf(selectedHour));
        viewBinding.wheelMinute.setCurrentItem(minutes.indexOf(selectedMinute));
        viewBinding.wheelSeconds.setCurrentItem(seconds.indexOf(selectedSecond));
    }

    private void selectTime() {
        if (onTimeSelectedListener != null) {
            onTimeSelectedListener.onTimeSelected(
                    selectedYear, selectedMonth, selectedDay,
                    selectedHour, selectedMinute, selectedSecond);
        }
        dismiss();
    }

    private void updateDayOfMonth() {
        Calendar calendar = new GregorianCalendar(selectedYear, selectedMonth - 1, 1);
        int dayOfMonCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        days.clear();
        for (int i = 1; i <= dayOfMonCount; i++) {
            days.add(i);
        }
        viewBinding.wheelDay.setViewAdapter(new CalendarWheelAdapter(days, "日"));
        viewBinding.wheelDay.setCurrentItem(0);
    }

    private class CalendarWheelAdapter extends AbstractWheelTextAdapter {

        private final String unitName;
        private final List<Integer> list;

        protected CalendarWheelAdapter(List<Integer> list) {
            this(list, "");
        }

        protected CalendarWheelAdapter(List<Integer> list, String unitName) {
            super(getContext());
            this.list = list;
            this.unitName = unitName == null ? "" : unitName;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return String.format(Locale.getDefault(), "%02d", list.get(index)) + unitName;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }
    }
}
