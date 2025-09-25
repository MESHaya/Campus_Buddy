package com.example.campus_buddy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import java.util.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        // ✅ DARK MODE SWITCH
        val darkModeSwitch: Switch = view.findViewById(R.id.darkModeSwitch)
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        darkModeSwitch.isChecked = isDarkMode
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("DarkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // ✅ NOTIFICATIONS SWITCH
        val notificationsSwitch: Switch = view.findViewById(R.id.notificationsSwitch)
        val notificationsEnabled = prefs.getBoolean("Notifications", true)
        notificationsSwitch.isChecked = notificationsEnabled
        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("Notifications", isChecked).apply()
            val msg = if (isChecked) "Notifications enabled" else "Notifications disabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        // ✅ LANGUAGE SPINNER
        val languageSpinner: Spinner = view.findViewById(R.id.languageSpinner)
        val savedLang = prefs.getString("Language", "English")

        // Pre-select the saved language
        val langArray = resources.getStringArray(R.array.languages)
        val savedIndex = langArray.indexOf(savedLang)
        if (savedIndex >= 0) {
            languageSpinner.setSelection(savedIndex)
        }

        languageSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLang = parent.getItemAtPosition(position).toString()
                prefs.edit().putString("Language", selectedLang).apply()

                // 🌍 Optional: Update app language instantly
                updateLanguage(selectedLang)

                Toast.makeText(requireContext(), "Language set to $selectedLang", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        return view
    }

    // ✅ Helper to update app language instantly
    private fun updateLanguage(language: String) {
        val locale = when (language) {
            "French" -> Locale.FRENCH
            "Spanish" -> Locale("es")
            else -> Locale.ENGLISH
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        // Refresh activity to apply language
        requireActivity().recreate()
    }
}
