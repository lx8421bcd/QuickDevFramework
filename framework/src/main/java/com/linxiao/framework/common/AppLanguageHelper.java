package com.linxiao.framework.common;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.linxiao.framework.preferences.AppPreferences;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * app language select helper class
 *
 * @author lx8421bcd
 * @since 2022-11-08
 */
public class AppLanguageHelper {

    public static class LanguageOption implements Serializable {

        private static final long serialVersionUID = 437485L;

        private String id;
        private final String displayName;
        private final Locale locale;

        public static String generateId(Locale locale) {
            return locale.getLanguage() + locale.getCountry();
        }

        public static LanguageOption followingSystem() {
            LanguageOption option = new LanguageOption(Resources.getSystem().getConfiguration().locale);
            option.id = FOLLOWING_SYSTEM;
            return option;
        }

        public LanguageOption(Locale locale) {
            this(locale, locale.getDisplayName());
        }

        public LanguageOption(Locale locale, String displayName) {
            this.id = generateId(locale);
            this.displayName = displayName;
            this.locale = locale;
        }

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Locale getLocale() {
            return locale;
        }

        @Override
        public String toString() {
            return "LanguageOption{" +
                    "id='" + id + '\'' +
                    ", displayName='" + displayName + '\'' +
                    ", locale=" + locale +
                    '}';
        }
    }

    public static class LanguageChangedEvent {

    }

    public static final String DEFAULT_APP_LANGUAGE = "English";
    public static final LanguageOption[] SUPPORTED_LANGUAGES = {
            new LanguageOption(Locale.SIMPLIFIED_CHINESE, "简体中文"),
            new LanguageOption(Locale.ENGLISH, "English"),

    };
    private static final String FOLLOWING_SYSTEM = "FOLLOWING_SYSTEM";
    private static final String SELECTED_LOCALE = "SELECTED_LOCALE";


    public static String getCurrentLangeName() {
        LanguageOption currentOption = getCurrentLanguageOption();
        String localId = LanguageOption.generateId(currentOption.locale);
        for (LanguageOption option : SUPPORTED_LANGUAGES) {
            if (option.getId().equals(localId)) {
                return option.displayName;
            }
        }
        return DEFAULT_APP_LANGUAGE;
    }

    public static LanguageOption getFollowingSystemOption() {
        return LanguageOption.followingSystem();
    }

    public synchronized static LanguageOption getCurrentLanguageOption() {
        LanguageOption cachedLocale = AppPreferences.getDefault().getSerializable(SELECTED_LOCALE);
        if (cachedLocale != null) {
            for (LanguageOption locale : SUPPORTED_LANGUAGES) {
                if (locale.getId().equals(cachedLocale.getId())) {
                    return locale;
                }
            }
        }
        return LanguageOption.followingSystem();
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
        Observable.just(0)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(o -> {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        })
        .subscribe();
    }

    public synchronized static void setLanguage(LanguageOption option) {
        AppPreferences.getDefault().put(SELECTED_LOCALE, option);
        Resources res = ContextProvider.get().getResources();
        changeResourcesConfig(res);
    }

    public synchronized static void updateLanguageSetting(Context context) {
        changeResourcesConfig(context.getResources());
    }

    public synchronized static void doOnContextGetResources(Resources res) {
        changeResourcesConfig(res);
    }

    private synchronized static void changeResourcesConfig(Resources res) {
        LanguageOption option = getCurrentLanguageOption();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = option.locale;
        res.updateConfiguration(conf, dm);
    }
}
