package com.linxiao.framework.biometric

import androidx.biometric.BiometricPrompt

interface BiometricAuthCallback {
    fun onSuccess(result: BiometricPrompt.AuthenticationResult)

    fun onError(exception: Throwable)

    fun onFailed()
}