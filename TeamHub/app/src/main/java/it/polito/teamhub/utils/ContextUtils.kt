package it.polito.teamhub.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

fun wrapContextLocale(context: Context, languageCode: String): ContextWrapper {
    val config = context.resources.configuration
    val sysLocale: Locale =
        getSystemLocale(config)

    if (languageCode.isNotEmpty() && !isSameLanguage(sysLocale.language, languageCode)) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        setSystemLocale(config, locale)
    }

    return ContextWrapper(context.createConfigurationContext(config))
}

private fun getSystemLocale(config: Configuration): Locale {
    return config.locales.get(0)
}

private fun setSystemLocale(config: Configuration, locale: Locale) {
    config.setLocale(locale)
}

private fun isSameLanguage(languageCode: String, sysLanguage: String): Boolean {
    return sysLanguage.equals(languageCode, ignoreCase = true)
}