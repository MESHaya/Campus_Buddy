package com.example.campus_buddy  // ✅ Choose one package name to use

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Calendar Button
        val calendarButton: Button = view.findViewById(R.id.btnOption1)
        calendarButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalendarFragment())
                .addToBackStack(null)
                .commit()
        }

        // Settings Button
        val settingsButton: Button = view.findViewById(R.id.btnOption6)
        settingsButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
