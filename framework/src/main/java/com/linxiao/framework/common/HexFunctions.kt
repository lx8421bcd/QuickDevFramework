package com.linxiao.framework.common

import java.math.BigDecimal
import java.math.BigInteger

/**
 * 16进制字符串处理
 *
 * @author lx8421bcd
 * @since 2023-03-10
 */

fun String.cleanHexPrefix(): String {
    return this.drop(if (this.startsWith("0x")) 2 else 0)
}
fun String.hexToInt(): Int {
    return this.cleanHexPrefix().toInt(16)
}

fun String.hexToLong(): Long {
    return this.cleanHexPrefix().toLong(16)
}

fun String.hexToBigInteger(): BigInteger {
    return BigInteger(this.cleanHexPrefix(), 16)
}

fun String.hexToBigDecimal(): BigDecimal {
    return BigDecimal(this.hexToBigInteger())
}

fun String.hexToByteArray(): ByteArray {
    val result = mutableListOf<Byte>()
    for (i in indices step 2) {
        val byteString = if (i + 2 > this.lastIndex) "0" + substring(i) else substring(i, i + 2)
        val byte = byteString.toInt(16)
        result.add(byte.toByte())
    }
    return result.toByteArray()
}

fun ByteArray.toHexString(prefix: Boolean = false): String {
    val hex = this.joinToString("") {
        it.toInt().and(0xff).toString(16).padStart(2, '0')
    }
    return "${if (prefix) "0x" else ""}$hex"
}
