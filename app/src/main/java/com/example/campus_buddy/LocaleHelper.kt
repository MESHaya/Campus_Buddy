package com.example.campus_buddy

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = getLocaleFromLanguage(language)
        Locale.setDefault(locale)
        return updateResources(context, locale)
    }

    // ADD THIS METHOD ↓
    private fun getLocaleFromLanguage(language: String): Locale {
        return when (language.lowercase()) {
            "english", "isingisi", "senyesemane", "engels" -> Locale.ENGLISH
            "afrikaans" -> Locale("af")
            "isizulu", "zulu" -> Locale("zu")
            "sesotho", "sotho" -> Locale("st")
            else -> Locale.ENGLISH
        }
    }

    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }

    fun onAttach(context: Context): Context {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = prefs.getString("Language", "English") ?: "English"
        return setLocale(context, language)
    }

    // ADD THIS METHOD ↓
    fun getNormalizedLanguage(language: String): String {
        return when (language.lowercase()) {
            "english", "isingisi", "senyesemane", "engels" -> "English"
            "afrikaans" -> "Afrikaans"
            "isizulu", "zulu" -> "isiZulu"
            "sesotho", "sotho" -> "SeSotho"
            else -> "English"
        }
    }
}