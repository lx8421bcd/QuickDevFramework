package com.linxiao.framework.common;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * 生物识别验证工具类
 * <p>
 * 用于快速调用系统指纹/面部识别API，获取授权，并对错误信息进行统一封装
 * </p>
 *
 * @author lx8421bcd
 * @since 2022-11-10
 */
public class BiometricHelper {

    public static class BiometricException extends IOException {
        public int code = 0;

        public BiometricException(int code) {
            this(code, "");
        }

        public BiometricException(int code, String message) {
            super(message);
            this.code = code;
        }
    }

    private static BiometricHelper instance = null;

    public static BiometricHelper getInstance() {
        if (instance == null) {
            synchronized (BiometricHelper.class) {
                if (instance == null) {
                    instance = new BiometricHelper();
                }
            }
        }
        return instance;
    }

    /**
     * check the current system has biometric hardware and the hardware is enabled
     *
     * @return hardware enabled
     */
    public boolean hardwareEnabled() {
        int checkResult =  BiometricManager.from(ContextProvider.get()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        // the hardware do not support biometric
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            return false;
        }
        // the system have biometric hardware, but the hardware disabled
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            return false;
        }
        return true;
    }

    /**
     * check the biometric authorization can use for current moment
     * <p>
     *  include most situations that biometric dialog can't open.
     *  e.t. no hardware, biometric message not enrolled,
     *  biometric authorization locked caused by retry too many times
     * </p>
     * @return biometric authorization enabled
     */
    public boolean canUseBiometricAuthorization() {
        int checkResult =  BiometricManager.from(ContextProvider.get()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        return checkResult == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public Observable<BiometricPrompt.AuthenticationResult> getAuthorization(
            Fragment fragment,
            BiometricPrompt.PromptInfo promptInfo
    ){
        return execAuthorization(fragment, promptInfo);
    }

    public Observable<BiometricPrompt.AuthenticationResult> getAuthorization(
            FragmentActivity activity,
            BiometricPrompt.PromptInfo promptInfo
    ){
        return execAuthorization(activity, promptInfo);
    }

    /**
     * start biometric authorization dialog and get authorization result after dialog close
     *
     * @return callback subject
     */
    private Observable<BiometricPrompt.AuthenticationResult> execAuthorization(Object context, BiometricPrompt.PromptInfo promptInfo) {
        int checkResult =  BiometricManager.from(ContextProvider.get()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            return Observable.error(new BiometricException(BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE));
        }
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE) {
            return Observable.error(new BiometricException(BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE));
        }
        if (checkResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            return Observable.error(new BiometricException(BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED));
        }
        BehaviorSubject<BiometricPrompt.AuthenticationResult> callbackSubject = BehaviorSubject.create();
        BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                String errText = errString + "(" + errorCode + ")";
                callbackSubject.onError(new BiometricException(errorCode, errText));
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callbackSubject.onNext(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Called when a biometric is valid but not recognized.
            }
        };
        BiometricPrompt biometricPrompt;
        if (context instanceof FragmentActivity) {
            biometricPrompt = new BiometricPrompt((FragmentActivity) context, authenticationCallback);
        }
        else {
            biometricPrompt = new BiometricPrompt((Fragment) context, authenticationCallback);
        }
        biometricPrompt.authenticate(promptInfo);
        return callbackSubject;
    }

}
