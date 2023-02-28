package com.linxiao.framework.language;

import androidx.annotation.StringRes;

import com.linxiao.framework.common.ContextProvider;

import java.io.Serializable;
import java.util.Locale;

/**
 * language locale object holder class
 * 
 * @author lx8421bcd
 * @since 2022-11-08
 */
public class LanguageOption implements Serializable {

    private static final long serialVersionUID = -1795833548072616784L;

    public static final String ID_FOLLOWING_SYSTEM = "FOLLOWING_SYSTEM";

    private String id;
    private String displayName = "";

    private int displayNameRes;

    private final Locale locale;

    public static String generateId(Locale locale) {
        return locale.getLanguage() + locale.getCountry();
    }

    public static LanguageOption followingSystem() {
        LanguageOption option = new LanguageOption(AppLanguageHelper.getSystemCurrentLocale());
        option.id = ID_FOLLOWING_SYSTEM;
        option.displayName = "Default";
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

    public LanguageOption(Locale locale, @StringRes int displayNameRes) {
        this.id = generateId(locale);
        this.locale = locale;
        this.displayNameRes = displayNameRes;
    }

    public boolean isFollowingSystemOption() {
        return ID_FOLLOWING_SYSTEM.equals(id);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        if (displayNameRes != 0) {
            displayName = ContextProvider.get().getString(displayNameRes);
        }
        return displayName;
    }

    public int getDisplayNameRes() {
        return displayNameRes;
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
