package com.linxiao.framework.net;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.linxiao.framework.common.BitmapUtil;
import com.linxiao.framework.common.ContextProvider;
import com.linxiao.framework.permission.PermissionException;
import com.linxiao.framework.permission.PermissionManager;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * simple image upload tool
 *
 * <p>
 * used to handle some simple image upload demand such like upload avatar.
 * default upload limit size is 10MB, you can change this attribute by using
 * {@link #setUploadLimitSize(int)}.
 * default User-Agent is application package name, use {@link #setUserAgent(String)}
 * to change it globally
 *
 * general procedure of image upload is set image local path and remote upload url,
 * execute upload program and get upload result, which contains uploaded image url.
 * so the onNext method of Subscriber will returns a JSONObject that convert by
 * http response body, considering that the response format is determined by the backend
 * and is actually unknown, this tool do not provide more result parsing
 *
 * </p>
 *
 * @author linxiao
 * @since 2019-12-04
 */
public class SimpleImageUploader {
    
    private static final String TAG = SimpleImageUploader.class.getSimpleName();
    
    private static final int DEFAULT_UPLOAD_LIMIT_SIZE = 10485760;  // default limit 10MB
    private static final String DEFAULT_USER_AGENT = ContextProvider.get().getPackageName();

    private static String mUserAgent = DEFAULT_USER_AGENT;
    private String filePath;
    private String uploadUrl;
    private String method = "POST";
    private String cookieString = null;
    private Map<String, Object> requestParams = new HashMap<>();
    private int limitSize = DEFAULT_UPLOAD_LIMIT_SIZE;

    private SimpleImageUploader(String filePath, String uploadUrl) {
        this.filePath = filePath;
        this.uploadUrl = uploadUrl;
    }

    /**
     * set User-Agent string
     */
    public static void setUserAgent(String userAgent) {
        mUserAgent = userAgent;
    }


    public static SimpleImageUploader newInstance(String filePath, String uploadUrl){
        return new SimpleImageUploader(filePath, uploadUrl);
    }

    /**
     * set upload limit size, image will be compressed that over this limit
     * @param limitSize limit size
     * */
    public SimpleImageUploader setUploadLimitSize(int limitSize) {
        this.limitSize = limitSize;
        return this;
    }

    /**
     * set upload request method, usually POST or GET
     * using POST by default
     * @param method method name
     * */
    public SimpleImageUploader setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * add additional request params
     * @param key key
     * @param value value
     */
    public SimpleImageUploader putRequestParams(String key, Object value) {
        requestParams.put(key, value);
        return this;
    }

    public SimpleImageUploader setCookieString(String cookieString) {
        this.cookieString = cookieString;
        return this;
    }

    public String getCookieString() {
        return cookieString;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public Observable<JSONObject> start() {
        if (!PermissionManager.hasSDCardPermission()) {
            return Observable.error(new PermissionException());
        }
        return Observable.create((ObservableOnSubscribe<JSONObject>) emitter -> {
            String BOUNDARY = UUID.randomUUID().toString();
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data";

            File uploadFile = new File(filePath);
            if (!uploadFile.exists()) {
                emitter.onError(new FileNotFoundException());
                return;
            }
            if (!uploadFile.isFile()) {
                emitter.onError(new FileNotFoundException("target is not a file"));
                return;
            }
            String fileName = uploadFile.getName();
            Log.d(TAG, "filePath = " + filePath);
            Log.d(TAG, "fileName = " + fileName);
            Bitmap uploadImg = BitmapUtil.getBitmap(filePath);
            int quality = BitmapUtil.getCompressQuality(uploadImg, uploadFile.length());
            Log.d(TAG, "quality: " + quality);
            Log.d(TAG, "limitSize: " + limitSize);
            uploadImg = BitmapUtil.matrixCompressByLimit(uploadImg, quality, limitSize);
            try {
                Log.d(TAG, "upload url: " + uploadUrl);
                URL url = new URL(uploadUrl);
                trustEveryone();
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod(method);
                connection.setRequestProperty("User-Agent", mUserAgent);
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Connection", "keep-alive");
                connection.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                if (!TextUtils.isEmpty(cookieString)) {
                    connection.setRequestProperty("Cookie", cookieString);
                }
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                for (String key : requestParams.keySet()) {
                    dos.writeBytes(PREFIX + BOUNDARY);
                    dos.writeBytes(LINE_END);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + LINE_END);
                    dos.writeBytes("Content-Type: text/plain" + LINE_END);
                    dos.writeBytes(LINE_END);
                    dos.writeBytes(String.valueOf(requestParams.get(key)));
                    dos.writeBytes(LINE_END);
                }
                dos.writeBytes(PREFIX + BOUNDARY);
                dos.writeBytes(LINE_END);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + LINE_END);
                dos.writeBytes("Content-Type: image/jpeg; charset=" + "UTF-8" + LINE_END);
                dos.writeBytes(LINE_END);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                uploadImg.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                InputStream is =  new ByteArrayInputStream(baos.toByteArray());
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.writeBytes(LINE_END);
                dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
                dos.flush();
                dos.close();
                int code = connection.getResponseCode();
                Log.d(TAG, "code: " + code);
                if (code != 200) {
                    emitter.onError(new IOException("upload failed(" + code + ")"));
                    return;
                }
                InputStream input = connection.getInputStream();
                StringBuilder sb1 = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                String result = sb1.toString();
                Log.d(TAG, "result: " + result);
                emitter.onNext(new JSONObject(result));
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        })
        .subscribeOn(Schedulers.io());
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
