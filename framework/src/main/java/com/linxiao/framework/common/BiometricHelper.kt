package com.linxiao.framework.common

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.IOException

/**
 * 生物识别验证工具类
 *
 *<p>
 * 用于快速调用系统指纹/面部识别API，获取授权，并对错误信息进行统一封装
 *</p>
 *
 * @author lx8421bcd
 * @since 2022-11-10
 */
object BiometricHelper {

    class BiometricException @JvmOverloads constructor(code: Int, message: String? = "") :
        IOException(message) {
        var code = 0
        init {
            this.code = code
        }
    }

    interface BiometricAuthCallback {
        fun onSuccess(result: BiometricPrompt.AuthenticationResult)

        fun onError(exception: Throwable)

        fun onFailed()
    }

    /**
     * check the current system has biometric hardware and the hardware is enabled
     *
     * @return hardware enabled
     */
    @JvmStatic
    fun hardwareEnabled(): Boolean {
        val checkResult = BiometricManager.from(ContextProvider.get()).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        // the hardware do not support biometric
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            return false
        }
        // the system have biometric hardware, but the hardware disabled
        return checkResult != BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
    }

    /**
     * check the biometric authorization can use for current moment
     *
     *
     * include most situations that biometric dialog can't open.
     * e.t. no hardware, biometric message not enrolled,
     * biometric authorization locked caused by retry too many times
     *
     * @return biometric authorization enabled
     */
    @JvmStatic
    fun canUseBiometricAuthorization(): Boolean {
        val checkResult = BiometricManager.from(ContextProvider.get()).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        return checkResult == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun getAuthorization(
        fragment: Fragment,
        promptInfo: PromptInfo,
        callback: BiometricAuthCallback
    ) {
        execAuthorization(fragment, promptInfo, callback)
    }

    fun getAuthorization(
        activity: FragmentActivity,
        promptInfo: PromptInfo,
        callback: BiometricAuthCallback
    ) {
        execAuthorization(activity, promptInfo, callback)
    }

    /**
     * start biometric authorization dialog and get authorization result after dialog close
     *
     * @return callback subject
     */
    private fun execAuthorization(
        context: Any,
        promptInfo: PromptInfo,
        callback: BiometricAuthCallback
    ) {
        val checkResult = BiometricManager.from(ContextProvider.get()).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            callback.onError(BiometricException(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE))
        }
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            callback.onError(BiometricException(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE))
        }
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            callback.onError(BiometricException(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED))
        }
        val authenticationCallback: BiometricPrompt.AuthenticationCallback =
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val errText = "$errString($errorCode)"
                    callback.onError(BiometricException(errorCode, errText))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    callback.onSuccess(result)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Called when a biometric is valid but not recognized.
                    // can be call multiple time during an authorization
                    callback.onFailed()
                }
            }
        val biometricPrompt: BiometricPrompt = if (context is FragmentActivity) {
            BiometricPrompt(context, authenticationCallback)
        } else {
            BiometricPrompt((context as Fragment), authenticationCallback)
        }
        biometricPrompt.authenticate(promptInfo)
    }
}