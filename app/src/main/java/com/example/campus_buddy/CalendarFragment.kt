package com.example.campus_buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.campus_buddy.databse.DatabaseHelper

class CalendarFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If you want to handle arguments, you can still do:
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            val param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        dbHelper = DatabaseHelper(requireContext())
        calendarView = view.findViewById(R.id.calendarView)

        // Listen for date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Fetch tasks with this due date
            val tasks = dbHelper.getTasksByDate(selectedDate)

            if (tasks.isNotEmpty()) {
                val taskTitles = tasks.joinToString("\n") { it.title }
                Toast.makeText(requireContext(), "Tasks:\n$taskTitles", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "No tasks due", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
