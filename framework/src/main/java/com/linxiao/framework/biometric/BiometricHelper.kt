package com.linxiao.framework.biometric

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.text.TextUtils
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.linxiao.framework.common.ContextProvider
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

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

    private const val CACHE_PREFERENCES = "PREF_BIOMETRIC"
    private const val PREF_ENROLL_CHANGED = "PREF_ENROLL_CHANGED"
    private const val KEYSTORE_ALIAS = "KEYSTORE_ALIAS"
    private const val HAS_BIOMETRIC_CACHE = "HAS_BIOMETRIC_CACHE"
    // for SAMSUNG check
    private const val SECRET_MESSAGE = "Very secret message"

    private val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    private val keystore = KeyStore.getInstance("AndroidKeyStore")
    private var cachedEnrolledChanged: Boolean
        get() {
            val pref = ContextProvider.get().getSharedPreferences(CACHE_PREFERENCES, Context.MODE_PRIVATE)
            return pref.getBoolean(PREF_ENROLL_CHANGED, false)
        }
        set(value) {
            val pref = ContextProvider.get().getSharedPreferences(CACHE_PREFERENCES, Context.MODE_PRIVATE)
            pref.edit().putBoolean(PREF_ENROLL_CHANGED, value).apply()
        }

    class AuthBuilder(private val promptInfo: PromptInfo) {

        private var failOnEnrollChanged = false

        fun setFailOnEnrollChanged(value: Boolean) {
            failOnEnrollChanged = value
        }
        fun execute(fragment: Fragment, callback: BiometricAuthCallback) {
            execAuthorization(fragment, promptInfo, callback)
        }

        fun execute(activity: FragmentActivity, callback: BiometricAuthCallback) {
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
            val checkResult = BiometricManager.from(ContextProvider.get()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            if (checkResult != BiometricManager.BIOMETRIC_SUCCESS) {
                callback.onError(BiometricException(checkResult))
                return
            }
            // check is biometric enrolled data changed
            if (failOnEnrollChanged && isBiometricEnrollChanged()) {
                callback.onError(BiometricEnrollChangedException())
                return
            }
            val authenticationCallback: BiometricPrompt.AuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val errText = "$errString($errorCode)"
                    callback.onError(BiometricException(errorCode, errText))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Called when a biometric is valid but not recognized.
                    // can be called multiple times during an authorization
                    callback.onFailed()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    result.cryptoObject?.cipher?.let {
                        if (checkEnrollChangedSAMSUNG(it)) {
                            cachedEnrolledChanged = true
                        }
                        if (failOnEnrollChanged && isBiometricEnrollChanged()) {
                            callback.onError(BiometricEnrollChangedException())
                            return
                        }
                    }
                    callback.onSuccess(result)
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

    /**
     * create a biometric authorization builder object
     *
     * @return builder object
     */
    fun createAuthorization(promptInfo: PromptInfo): AuthBuilder {
        return AuthBuilder(promptInfo)
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


    fun isBiometricEnrollChanged(): Boolean {
        if (cachedEnrolledChanged) {
            return true
        }
        createKey(false)
        val cipher = createCipher()
        val result = checkEnrollChangedByInitCipher(cipher)
        cachedEnrolledChanged = result
        return result
    }

    fun updateBiometricEnrollState() {
        cachedEnrolledChanged = false
        createKey(true)
    }

    private fun createCipher(): Cipher? {
        try {
            return Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun checkEnrollChangedByInitCipher(cipher: Cipher?): Boolean {
        val inputCipher = cipher?: createCipher()
        val result = try {
            keystore.load(null)
            val key = keystore.getKey(KEYSTORE_ALIAS, null) as SecretKey
            inputCipher!!.init(Cipher.ENCRYPT_MODE, key)
            false
            //指纹库是否发生了变化, 如果发生变化会抛KeyPermanentlyInvalidatedException
        } catch (e: KeyPermanentlyInvalidatedException) {
            true
        } catch (e: UnrecoverableKeyException) {
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return result
    }

    private fun checkEnrollChangedSAMSUNG(cipher: Cipher?): Boolean {
        var result = false
        cipher?.apply {
            try {
                cipher.doFinal(SECRET_MESSAGE.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                result = true
            }
        }
        return result
    }

    /**
     * @param createNewKey 是否创建新的密钥
     * @des 根据当前指纹库创建一个密钥
     */
    private fun createKey(createNewKey: Boolean) {
        val pref = ContextProvider.get().getSharedPreferences(CACHE_PREFERENCES, Context.MODE_PRIVATE)
        try {
            if (TextUtils.isEmpty(pref.getString(HAS_BIOMETRIC_CACHE, "")) || createNewKey) {
                val builder = KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                builder.setInvalidatedByBiometricEnrollment(true)
                keyGenerator.init(builder.build())
                keyGenerator.generateKey()
                pref.edit()
                    .putString(HAS_BIOMETRIC_CACHE, "KEY")
                    .apply()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}