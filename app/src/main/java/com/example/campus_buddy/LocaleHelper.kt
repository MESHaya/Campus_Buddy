package com.example.campus_buddy
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleHelper {

    fun setLocale(context: Context, language: String): Context {
        val locale = when (language) {
            "English" -> Locale.ENGLISH
            "Afrikaans" -> Locale("af")
            "isiZulu" -> Locale("zu")
            "SeSotho" -> Locale("st")  // Fixed to match strings.xml
            else -> Locale.ENGLISH
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }

    // ADD THIS METHOD ↓
    fun onAttach(context: Context): Context {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val language = prefs.getString("Language", "English") ?: "English"
        return setLocale(context, language)
    }
}