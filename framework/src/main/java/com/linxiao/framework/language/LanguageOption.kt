package com.linxiao.framework.language

import android.content.res.Resources
import java.io.Serial
import java.io.Serializable
import java.util.Locale

/**
 * language locale object holder class
 *
 * @author lx8421bcd
 * @since 2022-11-08
 */
class LanguageOption private constructor(
    val id: String,
    val locale: Locale
) : Serializable {

    companion object {

        @Serial
        private const val serialVersionUID = -1795833548072616784L

        const val ID_FOLLOWING_SYSTEM = "FOLLOWING_SYSTEM"

        @JvmStatic
        fun generateId(locale: Locale): String {
            return locale.language + locale.country
        }

        @JvmStatic
        fun followingSystem(): LanguageOption {
            return LanguageOption(ID_FOLLOWING_SYSTEM, Resources.getSystem().configuration.locale)
        }

        @JvmStatic
        fun newInstance(locale: Locale): LanguageOption {
            return LanguageOption(generateId(locale), locale)
        }
    }

    val isFollowingSystem = ID_FOLLOWING_SYSTEM == id

    var getDisplayName: () -> String = { id }

}
