package com.example.campus_buddy

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var prefs: android.content.SharedPreferences
    private var notificationsSwitch: SwitchMaterial? = null
    private var isInitializing = true // Flag to prevent initial trigger

    // Permission launcher for Android 13+ notifications
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

        // ----------------- APPLY SAVED DARK MODE ON LOAD -----------------
        val isDarkMode = prefs.getBoolean("DarkMode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // ----------------- BACK BUTTON -----------------
        val btnBack: ImageButton = view.findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // ----------------- DARK MODE SWITCH -----------------
        val darkModeSwitch: SwitchMaterial = view.findViewById(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = isDarkMode
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("DarkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // ----------------- NOTIFICATIONS SWITCH -----------------
        notificationsSwitch = view.findViewById(R.id.notificationsSwitch)
        val notificationsEnabled = prefs.getBoolean("Notifications", true)

        // Set initial state without triggering listener
        notificationsSwitch?.isChecked = notificationsEnabled

        notificationsSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                askNotificationPermission()
            } else {
                prefs.edit().putBoolean("Notifications", false).apply()
                Toast.makeText(requireContext(), "Notifications disabled ❌", Toast.LENGTH_SHORT).show()
            }
        }

        // ----------------- LANGUAGE SPINNER -----------------
        val languageSpinner: Spinner = view.findViewById(R.id.languageSpinner)
        val savedLang = prefs.getString("Language", "English") ?: "English"
        val langArray = resources.getStringArray(R.array.languages)
        val savedIndex = langArray.indexOf(savedLang)
        if (savedIndex >= 0) languageSpinner.setSelection(savedIndex, false)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isInitializing) {
                    isInitializing = false
                    return
                }

                val selectedLang = parent.getItemAtPosition(position).toString()
                val currentLang = prefs.getString("Language", "English")

                if (selectedLang != currentLang) {
                    prefs.edit().putString("Language", selectedLang).apply()
                    updateLanguage(selectedLang)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }

    // ----------------- NOTIFICATION PERMISSION -----------------
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    prefs.edit().putBoolean("Notifications", true).apply()
                    showTestNotification()
                    Toast.makeText(requireContext(), "Notifications enabled ✅", Toast.LENGTH_SHORT).show()
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
            // For older Android versions, enable notifications directly
            prefs.edit().putBoolean("Notifications", true).apply()
            showTestNotification()
            Toast.makeText(requireContext(), "Notifications enabled ✅", Toast.LENGTH_SHORT).show()
        }
    }

    // ----------------- TEST NOTIFICATION -----------------
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

        val notification = androidx.core.app.NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle("Campus Buddy")
            .setContentText("Notifications are enabled 🎉")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        notificationManager.notify(1, notification)
    }

    // ----------------- UPDATE LANGUAGE -----------------
    private fun updateLanguage(language: String) {
        // Set the locale in LocaleHelper
        LocaleHelper.setLocale(requireContext(), language)

        // Restart the entire activity to apply language changes immediately
        val intent = requireActivity().intent
        requireActivity().finish()
        startActivity(intent)

        // Optional: Add a fade animation for smoother transition
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}