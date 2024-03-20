package com.linxiao.framework.encrypt

import android.util.Base64
import android.util.Pair
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.Signature
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object RSAUtil {

    private const val KEY_ALGORITHM = "RSA"
    private const val KEY_SIZE = 1024

    @Throws(NoSuchAlgorithmException::class)
    fun genSecretKey(): Pair<String, String> {
        val keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyPairGenerator.initialize(KEY_SIZE)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val publicKeyByteArray = publicKey.encoded
        val privateKeyByteArray = privateKey.encoded
        val publicKeyString = Base64.encodeToString(publicKeyByteArray, Base64.DEFAULT)
        val privateKeyString = Base64.encodeToString(privateKeyByteArray, Base64.DEFAULT)
        return Pair(publicKeyString, privateKeyString)
    }

    @Throws(Exception::class)
    fun publicKeyEncrypt(data: String, publicKey: String?): String {
        val publicBytes = Base64.decode(publicKey, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(publicBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey = keyFactory.generatePublic(keySpec)
        val parameters = AlgorithmParameters.getInstance("OAEP", BouncyCastleProvider())
        val specification: AlgorithmParameterSpec = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        parameters.init(specification)
        val cipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider())
        cipher.init(Cipher.ENCRYPT_MODE, pubKey, parameters)
        val dataBytes = data.toByteArray()
        val encodedData = cipher.doFinal(dataBytes)
        return Base64.encodeToString(encodedData, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun privateKeyDecrypt(data: String?, privateKey: String?): String {
        val privateBytes = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(privateBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val priKey = keyFactory.generatePrivate(keySpec)
        val parameters = AlgorithmParameters.getInstance("OAEP", BouncyCastleProvider())
        val specification: AlgorithmParameterSpec = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        parameters.init(specification)
        val cipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider())
        cipher.init(Cipher.DECRYPT_MODE, priKey, parameters)
        val dataBytes = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(dataBytes)
        return String(decodedData)
    }

    @Throws(Exception::class)
    fun privateKeyEncrypt(data: String, privateKey: String?): String {
        val privateBytes = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(privateBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val priKey = keyFactory.generatePrivate(keySpec)
        val parameters = AlgorithmParameters.getInstance("OAEP", BouncyCastleProvider())
        val specification: AlgorithmParameterSpec = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        parameters.init(specification)
        val cipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider())
        cipher.init(Cipher.ENCRYPT_MODE, priKey, parameters)
        val dataBytes = data.toByteArray()
        val encodedData = cipher.doFinal(dataBytes)
        return Base64.encodeToString(encodedData, Base64.DEFAULT)
    }

    @Throws(Exception::class)
    fun publicKeyDecrypt(data: String?, publicKey: String?): String {
        val publicBytes = Base64.decode(publicKey, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(publicBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val pubKey = keyFactory.generatePublic(keySpec)
        val parameters = AlgorithmParameters.getInstance("OAEP", BouncyCastleProvider())
        val specification: AlgorithmParameterSpec = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA256,
            PSource.PSpecified.DEFAULT
        )
        parameters.init(specification)
        val cipher =
            Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProvider())
        cipher.init(Cipher.DECRYPT_MODE, pubKey, parameters)
        val dataBytes = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(dataBytes)
        return String(decodedData)
    }

    @Throws(Exception::class)
    fun privateKeySign(data: String, privateKey: String?): String {
        val privateBytes = Base64.decode(privateKey, Base64.DEFAULT)
        val keySpec = PKCS8EncodedKeySpec(privateBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        val priKey = keyFactory.generatePrivate(keySpec)
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(priKey)
        val dataBytes = data.toByteArray()
        signature.update(dataBytes)
        val signData = signature.sign()
        return Base64.encodeToString(signData, Base64.DEFAULT)
    }
}
