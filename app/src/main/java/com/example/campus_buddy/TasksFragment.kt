package com.example.campus_buddy

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.campus_buddy.databse.DatabaseHelper
import com.example.campusbuddy.data.Task
import java.text.SimpleDateFormat
import java.util.*

class TasksFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var tvTaskDate: TextView
    private lateinit var btnSaveTask: Button

    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        dbHelper = DatabaseHelper(requireContext())

        etTaskTitle = view.findViewById(R.id.etTaskTitle)
        etTaskDescription = view.findViewById(R.id.etTaskDescription)
        tvTaskDate = view.findViewById(R.id.tvTaskDate)
        btnSaveTask = view.findViewById(R.id.btnSaveTask)

        // Setup date picker click listener
        tvTaskDate.setOnClickListener {
            showDatePicker()
        }

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString().trim()
            val description = etTaskDescription.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
            } else if (selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show()
            } else {
                // Insert into database (make sure insertTask saves dueAt)
                val newTaskId = dbHelper.insertTask(title, description, selectedDate, "todo")

                // Optionally create a Task object if needed elsewhere
                val task = Task(
                    id = newTaskId.toString(),
                    userId = null,
                    title = title,
                    description = description,
                    category = null,
                    priority = "1",
                    dueAt = selectedDate,
                    status = "todo",
                    createdAt = System.currentTimeMillis().toString()
                )

                Toast.makeText(requireContext(), "Task saved successfully", Toast.LENGTH_SHORT).show()

                // Clear fields
                etTaskTitle.text.clear()
                etTaskDescription.text.clear()
                tvTaskDate.text = ""
                selectedDate = ""
            }
        }

        // Navigate to AllTasksFragment if you have a button for it
        val btnAllTasks = view.findViewById<Button>(R.id.btnAllTasks)
        btnAllTasks?.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AllTasksFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val cal = Calendar.getInstance()
                cal.set(year, month, day)

                selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                tvTaskDate.text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(cal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}
