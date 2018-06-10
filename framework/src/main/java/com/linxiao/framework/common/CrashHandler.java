package com.linxiao.framework.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.linxiao.framework.QDFApplication;
import com.linxiao.framework.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>全局捕获异常处理</h3> <br>
 * <h5>当程序发生Uncaught异常的时候,有该类来接管程序,并记录错误日志</h5>
 *
 * @author relish-wang
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private Context mContext;
    public static String TAG = "CrashHandler";
    private String exitInfo = "很抱歉,程序出现异常,即将退出!";

    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息

    private static class HolderClass {
        private static CrashHandler instance = new CrashHandler();
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return HolderClass.instance;
    }

    /**
     * 初始化
     *
     * @param context 上下文
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            if (!handleException(ex) && mDefaultHandler != null) {
                // 如果用户没有处理则让系统默认的异常处理器来处理
                mDefaultHandler.uncaughtException(thread, ex);
            } else {
                SystemClock.sleep(3000);
                // 退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex Throwable
     * @return true:如果处理了该异常信息;false:否则返回
     */
    private boolean handleException(Throwable ex) throws Exception {
        if (ex == null)
            return false;
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, exitInfo, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfoFile(ex);

        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx ctx
     */
    public void collectDeviceInfo(Context ctx) throws Exception {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName + "";
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.i(TAG, "an error occurred when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occurred when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex Throwable
     * @throws IOException 输入输出异常
     */
    private void saveCrashInfoFile(Throwable ex) throws IOException {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

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
        sb.append(result);
        Log.e(TAG, sb.toString());
        FileUtil.writeFile(
                FileUtil.SD_PATH + QDFApplication.getAppContext().getString(R.string.app_name),
                sb.toString(),
                true);
    }
}
