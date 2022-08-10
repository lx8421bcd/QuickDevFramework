package com.linxiao.framework.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.google.gson.reflect.TypeToken;
import com.linxiao.framework.net.RetrofitManager;
import com.linxiao.framework.preferences.AppPreferences;
import com.linxiao.framework.rx.RxSubscriber;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * <h3>全局捕获异常处理</h3> <br>
 * <h5>当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志</h5>
 *
 * @author relish-wang
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static String TAG = CrashHandler.class.getSimpleName();

    private static final String CACHE_KEY = "cache_report_data";

    private static CrashHandler instance;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private Thread.UncaughtExceptionHandler defaultCrashHandler;

    private CrashHandler() {
        defaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        uploadCachedLog();
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Map<String, Object> info = new HashMap<>();
        info.put("thread", t.getName());
        info.put("time", DateUtil.formatDate("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
        info.put("app_info", collectAppVersionInfo());
        info.put("device_info", collectDeviceInfo());
        info.put("network_info", collectNetworkInfo());
        info.put("stack_trace", getStackTraceStr(e));
        submitLog(info)
        .doOnError(ex -> {
            saveToLocal(info);
            defaultCrashHandler.uncaughtException(t, e);
        })
        .doOnNext(r -> defaultCrashHandler.uncaughtException(t, e))
        .subscribe(new RxSubscriber<>());
    }

    private Map<String, Object> collectAppVersionInfo() {
        Map<String, Object> ret = new ArrayMap<>();
        try {
            PackageManager pm = ContextProvider.get().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ContextProvider.get().getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                ret.put("versionName", pi.versionName);
                ret.put("versionCode", pi.versionCode);
            }
        } catch (Exception ignored) {}
//        ret.put("debug_mode", GlobalConfig.isDebugMode());
//        ret.put("beta_mode", GlobalConfig.isBetaMode());
        return ret;
    }

    private Map<String, Object> collectDeviceInfo() {
        Map<String, Object> ret = new ArrayMap<>();

        ret.put("MODEL", Build.MODEL);
        ret.put("ID", Build.ID);
        ret.put("HOST", Build.HOST);
        ret.put("OS_VERSION", Build.VERSION.RELEASE + "(API" + Build.VERSION.SDK_INT + ")");
        ret.put("DISPLAY", Build.DISPLAY);
        ret.put("PRODUCT", Build.PRODUCT);
        ret.put("MANUFACTURER", Build.MANUFACTURER);
        ret.put("CPU_ABI", Build.CPU_ABI);
        ret.put("SUPPORTED_ABIS", Arrays.asList(Build.SUPPORTED_ABIS));
        List<String> extraInfo = Arrays.asList("SOC_MODEL", "SOC_MANUFACTURER", "IS_EMULATOR");
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            if (extraInfo.contains(field.getName())) {
                try {
                    field.setAccessible(true);
                    ret.put(field.getName(), String.valueOf(field.get(null)));
                } catch (Exception ignored) { }
            }
        }
        return ret;
    }

    private Map<String, Object> collectNetworkInfo() {
        Map<String, Object> ret = new ArrayMap<>();
        ConnectivityManager manager = (ConnectivityManager) ContextProvider.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return ret;
        }
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        ret.put("connected", activeNetworkInfo.isConnected());
        ret.put("type", activeNetworkInfo.getTypeName());
        ret.put("detail_state", activeNetworkInfo.getDetailedState().toString());
        ret.put("extra_info", activeNetworkInfo.getExtraInfo());
        return ret;
    }

    private String getStackTraceStr(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.flush();
        printWriter.close();
        String result = writer.toString();
        Log.e(TAG, result);
        return result;
    }

    private void saveToLocal(Map<String, Object> info) {
        JSONObject reportData = GsonParser.toJSONObject(info);
        if (reportData != null) {
            AppPreferences.getDefault().put(CACHE_KEY, reportData.toString());
        }
    }

    private void uploadCachedLog() {
        String cacheStr = AppPreferences.getDefault().getString(CACHE_KEY, "");
        Log.d(TAG, "cached log: " + cacheStr);
        if (TextUtils.isEmpty(cacheStr)) {
            return;
        }
        Type convertType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> reportData = GsonParser.getParser().fromJson(cacheStr, convertType);
        if (reportData == null) {
            return;
        }
        AppPreferences.getDefault().remove(CACHE_KEY);
        submitLog(reportData).subscribe(new RxSubscriber<>());
    }

    private Observable<Object> submitLog(Map<String, Object> info) {
        return RetrofitManager.getCommonApi().jsonPost("", info)
        .subscribeOn(Schedulers.io())
        .map(resp -> resp);
    }
}
