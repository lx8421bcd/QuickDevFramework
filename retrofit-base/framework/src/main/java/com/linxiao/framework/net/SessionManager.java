package com.linxiao.framework.net;


import android.util.Log;

import com.linxiao.framework.support.log.Logger;
import com.linxiao.framework.support.preferences.PreferenceWrapper;

/**
 * Session management
 * Created by LinXiao on 2016-07-12.
 */
public class SessionManager {

    public static boolean HAS_SESSION_EXPIRED_TIME = false;
    //Session过期时间
    public static long SESSION_EXPIRED_TIME = 1000 * 60 * 3;

    private static String SESSION;

    private static long lastApiCallTime;

    /**
     * SessionManager 初始化
     * <p>从SharedPreferences读取缓存的Session和上一次Session调用时间</p>
     * */
    public static void init() {
        SESSION = PreferenceWrapper.getDefault().getString("session_cache", null);
        lastApiCallTime = PreferenceWrapper.getDefault().getLong("lastApiCallTime", 0L);
        Logger.d("SessionManager", "SessionManager init, session = " + SESSION);
    }

    /**
     * 判断Session是否过期
     * <p>计算当前时间与上次API调用时间之差,根据服务端配置的过期时间判断是否过期</p>
     * */
    public static boolean isSessionAvailable() {
        if(SESSION == null) {
            return false;
        }
        if(HAS_SESSION_EXPIRED_TIME) {
            long currTime = System.currentTimeMillis();
            return currTime - lastApiCallTime <= SESSION_EXPIRED_TIME;
        }
        return true;
    }

    /**
     * 设置上次Api调用时间
     * <p>每次刷新API调用时间都进行缓存,以免应用在有些退出情况下不能正确缓存</p>
     * */
    public static void setLastApiCallTime(long time) {
        lastApiCallTime = time;
        PreferenceWrapper.getDefault().put("lastApiCallTime", time);
    }
    /**
     * 设置Session
     * <p>每次设置Session都进行缓存,以免应用在有些退出情况下不能正确缓存</p>
     * */
    public static void setSession(String session) {
        SESSION = session;
        PreferenceWrapper.getDefault().put("session_cache", SESSION);
        Log.d(SessionManager.class.getSimpleName(), "Session = " + session);
    }

    /**
     * 清空Session
     * <p>清空程序内Session和缓存Session</p>
     * */
    public static void clearSession() {
        SESSION = null;
        PreferenceWrapper.getDefault().put("session_cache", null);
    }

    public static String getSession() {
        return SESSION;
    }

}
