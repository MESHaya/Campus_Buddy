package com.example.campus_buddy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class HomeActivity : AppCompatActivity() {

    // Function to load fragments into the container
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // replace FrameLayout with fragment
            .addToBackStack(null)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Load Dark Mode preference before UI
        val prefs = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("DarkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ✅ Load your HomeFragment by default when app opens
        if (savedInstanceState == null) {
            loadFragment(HomeFragment()) // your main/home screen fragment
        }
    }
}

