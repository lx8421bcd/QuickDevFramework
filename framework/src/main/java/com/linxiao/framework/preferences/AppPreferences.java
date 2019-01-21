package com.linxiao.framework.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.linxiao.framework.QDFApplication;

/**
 * SharedPreferences封装
 * <p>与PreferencesOperateObject配合封装SharedPreferences, 简化SharedPreferences存取操作</p>
 *
 * Created by linxiao on 2014/8/21.
 * */
public class AppPreferences {

    private AppPreferences() {}

    /**
     * 获取默认的SharedPreferences
     * <p>默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容</p>
     * */
    public static PreferenceOperator getDefault(Context context) {
        SharedPreferences sharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return new PreferenceOperator(sharedPreferences);
    }

    public static PreferenceOperator getDefault() {
        return getDefault(QDFApplication.getAppContext());
    }

    /**
     * 获取私有的SharedPreferences
     * <p>Private模式代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容</p>
     * */
    @Nullable
    public static PreferenceOperator getPrivate(Context context, String name) {
        return getPreferencesByMode(context, name, Context.MODE_PRIVATE);
    }

    public static PreferenceOperator getPrivate(String name) {
        return getPreferencesByMode(QDFApplication.getAppContext(), name, Context.MODE_PRIVATE);
    }

    /**
     * 获取Append模式的SharedPreferences
     * <p>Append模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件.</p>
     * */
    @Nullable
    public static PreferenceOperator getAppend(Context context, String name) {
        return getPreferencesByMode(context, name, Context.MODE_APPEND);
    }

    public static PreferenceOperator getAppend(String name) {
        return getPreferencesByMode(QDFApplication.getAppContext(), name, Context.MODE_APPEND);
    }


    @Nullable
    private static PreferenceOperator getPreferencesByMode(Context context, String name, int mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, mode);
        if (sharedPreferences != null) {
            return new PreferenceOperator(sharedPreferences);
        }
        return null;
    }

}