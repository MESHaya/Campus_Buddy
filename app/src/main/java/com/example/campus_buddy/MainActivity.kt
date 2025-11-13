package com.example.campus_buddy

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.*
import com.example.campus_buddy.notifications.TaskReminderWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit
import android.content.Context


class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle edge-to-edge insets (status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Schedule daily task reminder notifications
        scheduleDailyTaskReminders()

        // Handle navigation from notification intent
        val navigateTo = intent.getStringExtra("navigate_to")
        val fragmentToLoad = when (navigateTo) {
            "tasks" -> TasksFragment()
            "calendar" -> CalendarFragment()
            else -> HomeFragment()
        }

        // Load appropriate fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentToLoad)
                .commit()
        }

        // Setup Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_todo -> TasksFragment()
                R.id.nav_settings -> SettingsFragment()
                R.id.nav_emergency -> EmergencyFragment()
                R.id.nav_calendar -> CalendarFragment()
                else -> HomeFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
            true
        }
    }

    /**
     * Schedule daily task reminders at 8 AM every day
     */
    private fun scheduleDailyTaskReminders() {
        // Calculate initial delay to run at 8 AM
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 8)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)

            // If 8 AM already passed today, schedule for tomorrow
            if (timeInMillis < currentTime) {
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = calendar.timeInMillis - currentTime

        // Create periodic work request
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<TaskReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        // Enqueue the work
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_task_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }
}