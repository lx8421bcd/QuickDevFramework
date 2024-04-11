package com.linxiao.framework.dialog

import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingDialogFragment
import com.linxiao.framework.databinding.DialogWheelTimeSelectBinding
import com.linxiao.framework.widget.WheelDateTimePickerView

class WheelDateTimePickerDialog : SimpleViewBindingDialogFragment<DialogWheelTimeSelectBinding>() {

    fun interface OnConfigDateTimePicker {
        fun onConfig(pickerView: WheelDateTimePickerView)
    }

    var title: String = ""

    var onTimeSelectedListener: WheelDateTimePickerView.OnTimeSelectedListener? = null
    var onConfigDateTimePicker: OnConfigDateTimePicker? = null

    init {
        bottomSheetStyle = true
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.apply {
            dismiss()
            tvConfirm.setOnClickListener { v: View? ->
                onTimeSelectedListener?.onTimeSelected(
                    datetimePicker.selectedYear,
                    datetimePicker.selectedMonth,
                    datetimePicker.selectedDay,
                    datetimePicker.selectedHour,
                    datetimePicker.selectedMinute,
                    datetimePicker.selectedSecond,
                )
            }
            tvCancel.setOnClickListener { v: View? -> dismiss() }
            // picker config
            onConfigDateTimePicker?.onConfig(datetimePicker)
        }
    }

}
