package com.example.campus_buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnCalendar = view.findViewById<Button>(R.id.btnOption1)
        val btnTasks = view.findViewById<Button>(R.id.btnOption2)
        val btnAttendance = view.findViewById<Button>(R.id.btnOption3)
        val btnMap = view.findViewById<Button>(R.id.btnOption4)
        val btnEmergency = view.findViewById<Button>(R.id.btnOption5)
        val btnSettings = view.findViewById<Button>(R.id.btnOption6)

        // Navigate to Calendar
        btnCalendar.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CalendarFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Tasks
        btnTasks.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, TasksFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Attendance
        btnAttendance.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AttendanceFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Map
        btnMap.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MapFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Emergency (can reuse AttendanceFragment or make EmergencyFragment)
        btnEmergency.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EmergencyFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to Settings (assuming SettingsFragment exists)
        btnSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
