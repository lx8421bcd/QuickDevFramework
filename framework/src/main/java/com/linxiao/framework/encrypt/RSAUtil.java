package com.linxiao.framework.encrypt;

import android.util.Base64;
import android.util.Pair;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class RSAUtil {
    public static final String KEY_ALGORITHM = "RSA";

    private static final int KEY_SIZE = 1024;

    public static Pair<String, String> genSecretKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        byte[] publicKeyByteArray = publicKey.getEncoded();
        byte[] privateKeyByteArray = privateKey.getEncoded();
        String publicKeyString = Base64.encodeToString(publicKeyByteArray, Base64.DEFAULT);
        String privateKeyString = Base64.encodeToString(privateKeyByteArray, Base64.DEFAULT);
        return new Pair<>(publicKeyString, privateKeyString);
    }

    public static String publicKeyEncrypt(String data, String publicKey) throws Exception {
        byte[] publicBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("OAEP", new BouncyCastleProvider());
        AlgorithmParameterSpec specification = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        parameters.init(specification);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey, parameters);
        byte[] dataBytes = data.getBytes();
        byte[] encodedData = cipher.doFinal(dataBytes);
        return Base64.encodeToString(encodedData, Base64.DEFAULT);
    }

    public static String privateKeyDecrypt(String data, String privateKey) throws Exception {
        byte[] privateBytes = Base64.decode(privateKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("OAEP", new BouncyCastleProvider());
        AlgorithmParameterSpec specification = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        parameters.init(specification);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", new BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, priKey, parameters);
        byte[] dataBytes = Base64.decode(data, Base64.DEFAULT);
        byte[] decodedData = cipher.doFinal(dataBytes);
        return new String(decodedData);
    }

    public static String privateKeyEncrypt(String data, String privateKey) throws Exception {
        byte[] privateBytes = Base64.decode(privateKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("OAEP", new BouncyCastleProvider());
        AlgorithmParameterSpec specification = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        parameters.init(specification);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", new BouncyCastleProvider());
        cipher.init(Cipher.ENCRYPT_MODE, priKey, parameters);
        byte[] dataBytes = data.getBytes();
        byte[] encodedData = cipher.doFinal(dataBytes);
        return Base64.encodeToString(encodedData, Base64.DEFAULT);
    }

    public static String publicKeyDecrypt(String data, String publicKey) throws Exception {
        byte[] publicBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("OAEP", new BouncyCastleProvider());
        AlgorithmParameterSpec specification = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        parameters.init(specification);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", new BouncyCastleProvider());
        cipher.init(Cipher.DECRYPT_MODE, pubKey, parameters);
        byte[] dataBytes = Base64.decode(data, Base64.DEFAULT);
        byte[] decodedData = cipher.doFinal(dataBytes);
        return new String(decodedData);
    }

    public static String privateKeySign(String data, String privateKey) throws Exception {
        byte[] privateBytes = Base64.decode(privateKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(priKey);
        byte[] dataBytes = data.getBytes();
        signature.update(dataBytes);
        byte[] signData = signature.sign();
        return Base64.encodeToString(signData, Base64.DEFAULT);
    }
}
