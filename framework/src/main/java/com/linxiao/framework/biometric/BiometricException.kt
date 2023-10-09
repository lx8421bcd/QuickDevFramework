package com.linxiao.framework.biometric

import java.io.IOException

open class BiometricException @JvmOverloads constructor(code: Int, message: String? = "") : IOException(message) {
    var code = 0
    init {
        this.code = code
    }
}