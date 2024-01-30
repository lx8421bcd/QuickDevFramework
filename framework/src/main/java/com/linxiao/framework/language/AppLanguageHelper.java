package com.linxiao.framework.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;


import com.linxiao.framework.common.ContextProviderKt;
import com.linxiao.framework.preferences.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * app language select helper class
 *
 * @author lx8421bcd
 * @since 2022-11-08
 */
public class AppLanguageHelper {

    public static final List<LanguageOption> SUPPORTED_LANGUAGES = new ArrayList<>();

    static {
        initSupportLanguages();
    }

    public static void initSupportLanguages() {
        SUPPORTED_LANGUAGES.clear();
        SUPPORTED_LANGUAGES.add(new LanguageOption(Locale.ENGLISH, "English"));
        SUPPORTED_LANGUAGES.add(new LanguageOption(Locale.TRADITIONAL_CHINESE, "简体中文"));
    }

    private static final String PREF_SELECTED_LOCALE = "SELECTED_LOCALE";

    private static LanguageOption currentLanguageOption = null;

    public static Locale getSystemCurrentLocale() {
        return Resources.getSystem().getConfiguration().locale;
    }

    public static LanguageOption getFollowingSystemOption() {
        return LanguageOption.followingSystem();
    }

    private synchronized static LanguageOption getCachedLanguageOption() {
        LanguageOption cachedLocale = AppPreferences.getDefault().getSerializable(PREF_SELECTED_LOCALE);
        if (cachedLocale != null) {
            for (LanguageOption locale : SUPPORTED_LANGUAGES) {
                if (locale.getId().equals(cachedLocale.getId())) {
                    return locale;
                }
            }
        }
        return getFollowingSystemOption();
    }

    public synchronized static LanguageOption getCurrentLanguageOption() {
        if (currentLanguageOption == null) {
            currentLanguageOption = getCachedLanguageOption();
        }
        return currentLanguageOption;
    }

    public synchronized static void setLanguageFollowingSystem() {
        setLanguage(LanguageOption.followingSystem());
    }

    public synchronized static void setLanguageWithBroadcast(LanguageOption option) {
        setLanguage(option);
        EventBus.getDefault().post(new LanguageChangedEvent());
    }

    public synchronized static void setLanguageWithRestart(LanguageOption option) {
        setLanguage(option);
        Single.just(0)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess(o -> {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        })
        .subscribe();
    }

    public synchronized static void setLanguage(LanguageOption option) {
        AppPreferences.getDefault().put(PREF_SELECTED_LOCALE, option);
        currentLanguageOption = option;
        Resources res = ContextProviderKt.getGlobalContext().getResources();
        changeResourcesConfig(res);
    }

    public synchronized static void updateLanguageSetting(Context context) {
        changeResourcesConfig(context.getResources());
    }

    public synchronized static void doOnContextGetResources(Resources res) {
        changeResourcesConfig(res);
    }

    private synchronized static void changeResourcesConfig(Resources res) {
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = getCurrentLanguageOption().getLocale();
        res.updateConfiguration(conf, dm);
    }
}
