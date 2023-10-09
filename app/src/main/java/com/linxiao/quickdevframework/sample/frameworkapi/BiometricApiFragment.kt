package com.linxiao.quickdevframework.sample.frameworkapi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import com.linxiao.framework.biometric.BiometricAuthCallback
import com.linxiao.framework.biometric.BiometricEnrollChangedException
import com.linxiao.framework.biometric.BiometricHelper
import com.linxiao.framework.common.ToastAlert
import com.linxiao.quickdevframework.databinding.FragmentBiometricApiBinding

/**
 * class summary
 * <p>
 *  usage and notices
 * </p>
 *
 * @author lx8421bcd
 * @since 2023-10-10
 */
class BiometricApiFragment : Fragment() {

    private val viewBinding by lazy {
        return@lazy FragmentBiometricApiBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

    }

    private fun initViews() {
        viewBinding.btnTestAuth.setOnClickListener {
            execAuth()
        }
        viewBinding.btnCheckEnrollChanged.setOnClickListener {
            checkEnrollChanged()
        }
    }

    private fun execAuth() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("验证已录入的生物识别信息")
            .setSubtitle("验证以开启生物识别信息验证")
            .setNegativeButtonText("取消")
            .build()
        BiometricHelper.createAuthorization(promptInfo)
        .setFailOnEnrollChanged(viewBinding.cbCheckChange.isChecked)
        .execute(this, object : BiometricAuthCallback {

            override fun onSuccess(result: BiometricPrompt.AuthenticationResult) {
                ToastAlert.show("验证成功，result: $result")
            }

            override fun onError(exception: Throwable) {
                if (exception is BiometricEnrollChangedException) {
                    ToastAlert.show("检测到指纹信息已改变")
                }
                else {
                    ToastAlert.show(exception.message)
                }
            }

            override fun onFailed() {

            }
        })
    }

    private fun checkEnrollChanged() {
        if (BiometricHelper.isBiometricEnrollChanged()) {
            ToastAlert.show("检测到指纹信息已改变")
        }
        else {
            ToastAlert.show("未检测到指纹信息改变")
        }
    }
}