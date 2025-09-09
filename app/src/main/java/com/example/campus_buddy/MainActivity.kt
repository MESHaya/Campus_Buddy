package com.example.campus_buddy

import com.example.campus_buddy.R

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


    class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.setOnItemSelectedListener { item: MenuItem ->
                var selectedFragment: Fragment? = null
                when (item.itemId) {
                    R.id.nav_home -> selectedFragment = Home()
                    R.id.nav_calendar -> selectedFragment = CalendarFragment()
                    R.id.nav_attendance -> selectedFragment = AttendanceFragment()
                    R.id.nav_todo -> selectedFragment = TasksFragment()
                    R.id.nav_maps -> selectedFragment = MapFragment()
                }

                if (selectedFragment != null) {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container, selectedFragment
                    ).commit()
                }
                true
            }

            // Default fragment
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_container, Home()
                ).commit()
            }
        }
    }
