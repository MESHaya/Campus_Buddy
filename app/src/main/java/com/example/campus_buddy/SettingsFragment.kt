package com.example.campus_buddy

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var prefs: android.content.SharedPreferences
    private var notificationsSwitch: Switch? = null

    //Permission launcher for Android 13+ notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            prefs.edit().putBoolean("Notifications", true).apply()
            Toast.makeText(requireContext(), "Notifications enabled ✅", Toast.LENGTH_SHORT).show()
            showTestNotification()
            notificationsSwitch?.isChecked = true
        } else {
            prefs.edit().putBoolean("Notifications", false).apply()
            Toast.makeText(requireContext(), "Notifications denied ❌", Toast.LENGTH_SHORT).show()
            notificationsSwitch?.isChecked = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        prefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        //DARK MODE SWITCH
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

        //NOTIFICATIONS SWITCH
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch)
        val notificationsEnabled = prefs.getBoolean("Notifications", true)
        notificationsSwitch?.isChecked = notificationsEnabled
        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                askNotificationPermission()
            } else {
                prefs.edit().putBoolean("Notifications", false).apply()
                Toast.makeText(requireContext(), "Notifications disabled ❌", Toast.LENGTH_SHORT).show()
            }
        }

        //LANGUAGE SPINNER
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

    //Ask for notification permission (Android 13+)
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    prefs.edit().putBoolean("Notifications", true).apply()
                    showTestNotification()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(requireContext(), "Notification permission required", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // For older Android versions, just enable directly
            prefs.edit().putBoolean("Notifications", true).apply()
            showTestNotification()
        }
    }

    //Show a simple test notification
    private fun showTestNotification() {
        val channelId = "settings_channel"
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Settings Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle("Campus Buddy")
            .setContentText("Notifications are enabled 🎉")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(1, notification)
    }

    //Helper to update app language instantly
    private fun updateLanguage(language: String) {
        val locale = when (language) {
            "French" -> Locale.FRENCH
            "Spanish" -> Locale("es")
            else -> Locale.ENGLISH
        }
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        requireActivity().baseContext.resources.updateConfiguration(
            config,
            requireActivity().baseContext.resources.displayMetrics
        )

        // Refresh activity to apply language
        requireActivity().recreate()
    }
}
