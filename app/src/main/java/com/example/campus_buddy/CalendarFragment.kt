package com.example.campus_buddy

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campus_buddy.databse.DatabaseHelper
import com.example.campus_buddy.EventAdapter
import com.example.campus_buddy.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CalendarFragment<T> : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: EventAdapter

    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        db = DatabaseHelper(requireContext())
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView = view.findViewById(R.id.recyclerViewEvents)
        fabAdd = view.findViewById(R.id.fabAddEvent)

        adapter = EventAdapter(emptyList()) { event ->
            showEventOptions(event)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Default selected date = today
        selectedDate = android.text.format.DateFormat.format("yyyy-MM-dd", java.util.Date()).toString()
        loadEvents(selectedDate)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            loadEvents(selectedDate)
        }

        fabAdd.setOnClickListener {
            showAddEventDialog()
        }

        return view
    }

    private fun loadEvents(date: String) {
        val events = db.getEventsByDate(date)
        adapter.updateEvents(events)
    }

    private fun showAddEventDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Event")

        val layout = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = layout.findViewById<EditText>(R.id.etEventTitle)
        val etTime = layout.findViewById<EditText>(R.id.etEventTime)
        val etDescription = layout.findViewById<EditText>(R.id.etEventDescription)

        builder.setView(layout)
        builder.setPositiveButton("Save") { _, _ ->
            val title = etTitle.text.toString()
            val time = etTime.text.toString()
            val desc = etDescription.text.toString()

            if (title.isNotEmpty()) {
                db.insertEvent(title, selectedDate, time, desc)
                loadEvents(selectedDate)
                Toast.makeText(requireContext(), "Event added", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showEventOptions(event: Event) {
        val options = arrayOf("Edit", "Delete")
        AlertDialog.Builder(requireContext())
            .setTitle(event.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditEventDialog(event)
                    1 -> {
                        db.deleteEvent(event.id)
                        loadEvents(selectedDate)
                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }.show()
    }

    private fun showEditEventDialog(event: Event) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Edit Event")

        val layout = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val etTitle = layout.findViewById<EditText>(R.id.etEventTitle)
        val etTime = layout.findViewById<EditText>(R.id.etEventTime)
        val etDescription = layout.findViewById<EditText>(R.id.etEventDescription)

        etTitle.setText(event.title)
        etTime.setText(event.time)
        etDescription.setText(event.description)

        builder.setView(layout)
        builder.setPositiveButton("Update") { _, _ ->
            val updatedEvent = event.copy(
                title = etTitle.text.toString(),
                time = etTime.text.toString(),
                description = etDescription.text.toString()
            )
            db.updateEvent(updatedEvent)
            loadEvents(selectedDate)
            Toast.makeText(requireContext(), "Event updated", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
