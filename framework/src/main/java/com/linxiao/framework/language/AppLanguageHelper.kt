package com.linxiao.framework.language

import android.content.Context
import android.content.res.Resources
import android.os.Process
import com.linxiao.framework.common.globalContext
import com.linxiao.framework.language.LanguageOption.Companion.followingSystem
import com.linxiao.framework.language.LanguageOption.Companion.newInstance
import com.linxiao.framework.preferences.AppPreferences
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.Locale

/**
 * app language select helper class
 *
 * @author lx8421bcd
 * @since 2022-11-08
 */
object AppLanguageHelper {

    private const val PREF_SELECTED_LOCALE = "SELECTED_LOCALE"

    @JvmStatic
    val SUPPORTED_LANGUAGES: MutableList<LanguageOption> = ArrayList()

    private var currentLanguageOption: LanguageOption? = null

    fun getSystemCurrentLocale(): Locale {
        return Resources.getSystem().configuration.locale
    }

    fun getFollowingSystemOption(): LanguageOption {
        return followingSystem()
    }

    @Synchronized
    private fun getCachedLanguageOption(): LanguageOption {
        val cachedLocale = AppPreferences.getDefault()
            .getSerializable<LanguageOption>(PREF_SELECTED_LOCALE)
        if (cachedLocale != null) {
            for (locale in SUPPORTED_LANGUAGES) {
                if (locale.id == cachedLocale.id) {
                    return locale
                }
            }
        }
        return getFollowingSystemOption()
    }

    @Synchronized
    fun getCurrentLanguageOption(): LanguageOption? {
        if (currentLanguageOption == null) {
            currentLanguageOption = getCachedLanguageOption()
        }
        return currentLanguageOption
    }

    @Synchronized
    fun setLanguageFollowingSystem() {
        setLanguage(followingSystem())
    }

    @Synchronized
    fun setLanguageWithBroadcast(option: LanguageOption?) {
        setLanguage(option)
        EventBus.getDefault().post(LanguageChangedEvent())
    }

    @Synchronized
    fun setLanguageWithRestart(option: LanguageOption?) {
        setLanguage(option)
        Single.just(0)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnSuccess { o: Int? ->
            Process.killProcess(Process.myPid())
            System.exit(0)
        }
        .subscribe()
    }

    @Synchronized
    fun setLanguage(option: LanguageOption?) {
        AppPreferences.getDefault().put(PREF_SELECTED_LOCALE, option)
        currentLanguageOption = option
        val res = globalContext.resources
        changeResourcesConfig(res)
    }

    @Synchronized
    fun updateLanguageSetting(context: Context) {
        changeResourcesConfig(context.resources)
    }

    @Synchronized
    fun doOnContextGetResources(res: Resources) {
        val conf = res.configuration
        conf.locale = getCurrentLanguageOption()!!.locale
    }

    @Synchronized
    private fun changeResourcesConfig(res: Resources) {
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = getCurrentLanguageOption()!!.locale
        res.updateConfiguration(conf, dm)
    }
}
