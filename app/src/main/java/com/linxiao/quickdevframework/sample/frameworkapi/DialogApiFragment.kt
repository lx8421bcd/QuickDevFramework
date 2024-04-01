package com.linxiao.quickdevframework.sample.frameworkapi

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.ToastAlert.showToast
import com.linxiao.framework.dialog.AlertDialogFragment
import com.linxiao.framework.dialog.showAlert
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.FragmentDialogApiBinding

class DialogApiFragment : SimpleViewBindingFragment<FragmentDialogApiBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnShowAlertDialog.setOnClickListener { v: View? -> onShowAlertDialogClick(v) }
        viewBinding.btnShowSimpleDialog.setOnClickListener { v: View? -> onSimpleDialogClick(v) }
        viewBinding.btnShowOnStartActivity.setOnClickListener { v: View? ->
            onShowStartActivityClick(
                v
            )
        }
        viewBinding.btnShowBottomDialog.setOnClickListener { v: View? -> onShowBottomDialogClick(v) }
        viewBinding.btnShowTopDialog.setOnClickListener { v: View? -> onShowTopDialogClick(v) }
    }

    fun onShowAlertDialogClick(v: View?) {
        val dialogFragment = AlertDialogFragment()
        if (viewBinding.rbTypeString.isChecked) {
            dialogFragment.setMessage(getString(R.string.sample_dialog_message))
        }
        if (viewBinding.rbTypeHtmlString.isChecked) {
            dialogFragment.setContentHtml(
                """
                <ul>
                    <li>html unordered list item 1</li>
                    <li>html unordered list item 2</li>
                    <li>html unordered list item 3</li>
                </ul>
                
                """.trimIndent()
            )
        }
        if (viewBinding.rbTypeHtmlLink.isChecked) {
            dialogFragment.setContentLink("https://www.google.com")
        }
        dialogFragment.setPositiveButton(getString(R.string.sample_positive)) { dialogInterface: DialogInterface, i: Int ->
            showToast(context, getString(R.string.positive_click))
            dialogInterface.dismiss()
        }
        dialogFragment.setNegativeButton(getString(R.string.sample_negative)) { dialogInterface: DialogInterface, i: Int ->
            showToast(context, getString(R.string.negative_click))
            dialogInterface.dismiss()
        }
        dialogFragment.setDialogCancelable(false)
        dialogFragment.show(getChildFragmentManager())
    }

    fun onSimpleDialogClick(v: View?) {
        showAlert(getString(R.string.sample_dialog_message))
    }

    fun onShowStartActivityClick(v: View?) {
        startActivity(Intent(activity, NotificationTargetActivity::class.java))
        showAlert("dialog after start activity")
    }

    fun onShowTopDialogClick(v: View?) {
        val backServiceIntent = Intent(activity, BackgroundService::class.java)
        requireActivity().startService(backServiceIntent)
    }

    fun onShowBottomDialogClick(v: View?) {
        val dialogFragment = SampleBottomDialogFragment()
        dialogFragment.show(getChildFragmentManager(), "SampleDialog")
    }
}
