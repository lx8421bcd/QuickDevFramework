package com.linxiao.quickdevframework.sample.frameworkapi

import android.Manifest
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.linxiao.framework.architecture.SimpleViewBindingFragment
import com.linxiao.framework.common.ApplicationUtil.openAppDetail
import com.linxiao.framework.dialog.AlertDialogFragment
import com.linxiao.framework.dialog.showAlert
import com.linxiao.framework.permission.PermissionRequestHelper
import com.linxiao.framework.permission.PermissionUtil
import com.linxiao.framework.permission.PermissionUtil.getPermissionGroupName
import com.linxiao.framework.permission.RequestPermissionCallback
import com.linxiao.quickdevframework.R
import com.linxiao.quickdevframework.databinding.FragmentPermissionApiBinding

class PermissionApiFragment : SimpleViewBindingFragment<FragmentPermissionApiBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.btnRequestSample.setOnClickListener { v: View? -> onRequestPermissionClick(v) }
        viewBinding.btnRequestWithRationale.setOnClickListener { v: View? -> onRationaleClick(v) }
        viewBinding.btnDoOnProhibited.setOnClickListener { v: View? -> OnProhibitedClick(v) }
        viewBinding.btnRequestAlertWindow.setOnClickListener { v: View? -> onReqSysAlertClick(v) }
        viewBinding.btnRequestWriteSettings.setOnClickListener { v: View? -> onReqWriteSettingsClick(v) }
    }

    fun onRequestPermissionClick(v: View?) {
        PermissionRequestHelper.create()
            .requestAudioRecord()
            .perform(requireActivity(), object : RequestPermissionCallback {
                override fun onGranted() {
                    showAlert("权限已授予")
                }

                override fun onDenied() {
                    showAlert("未授予权限")
                }
            })
    }

    fun onRationaleClick(v: View?) {
        PermissionRequestHelper.create()
            .addRequestPermission(Manifest.permission.SEND_SMS)
            .showRationaleBeforeRequest("请授予发送短信权限权限以启用功能")
            .perform(requireActivity(), object : RequestPermissionCallback {
                override fun onGranted() {
                    showAlert("权限已授予")
                }

                override fun onDenied() {
                    showAlert("未授予权限")
                }
            })
    }

    fun OnProhibitedClick(v: View?) {
        PermissionRequestHelper.create()
        .addRequestPermission(Manifest.permission.READ_PHONE_STATE)
        .showRationaleBeforeRequest("请两次以上请求申请权限然后勾选\"不再提醒\"查看功能")
        .doOnProhibited { permission: String ->
            showPermissionProhibitedDialog(
                requireActivity(),
                PermissionUtil.getPermissionName(requireActivity(), permission)
            )
        }
        .perform(requireActivity(), object : RequestPermissionCallback {
            override fun onGranted() {
                showAlert("权限已授予")
            }

            override fun onDenied() {
                showAlert("未授予权限")
            }
        })
    }

    fun onReqSysAlertClick(v: View?) {
        PermissionRequestHelper.create()
            .requestManageOverlayPermission()
            .perform(requireActivity(), object : RequestPermissionCallback {
                override fun onGranted() {
                    showAlert("权限已授予")
                }

                override fun onDenied() {
                    showAlert("未授予权限")
                }
            })
    }

    fun onReqWriteSettingsClick(v: View?) {
        PermissionRequestHelper.create()
        .requestManageOverlayPermission()
        .perform(requireActivity(), object : RequestPermissionCallback {
            override fun onGranted() {
                showAlert("权限已授予")
            }

            override fun onDenied() {
                showAlert("未授予权限")
            }
        })
    }

    fun showPermissionProhibitedDialog(context: FragmentActivity, permission: String) {
        val permissionGroupName = getPermissionGroupName(context, permission)
        val message = permissionGroupName + context.getString(R.string.toast_permission_denied)
        val dialogFragment = AlertDialogFragment()
        dialogFragment.setMessage(message)
        .setPositiveButton { dialog: DialogInterface, which: Int ->
            // jump to application detail
            openAppDetail(context)
            dialog.dismiss()
        }
        .setNegativeButton { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        .show(context.supportFragmentManager)
    }
}
