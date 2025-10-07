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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var recyclerTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

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
        recyclerTasks = view.findViewById(R.id.recyclerTasks)

        // Setup RecyclerView with Task list
        adapter = TaskAdapter(taskList) { updatedTask ->
            dbHelper.updateTaskStatus(updatedTask.id, updatedTask.status)
        }
        recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        recyclerTasks.adapter = adapter

        // Sample task for testing
        taskList.add(Task("1", "Sample Task", "This is a test", "High", "2025-10-01", "Due", "2025-09-30"))
        adapter.notifyDataSetChanged()

        // Setup date picker click listener
        tvTaskDate.setOnClickListener {
            showDatePicker()
        }

        // Load tasks from DB on start
        loadTasksFromDatabase()

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString()
            val description = etTaskDescription.text.toString()
            val dueDate = selectedDate

            if (title.isNotEmpty() && dueDate.isNotEmpty()) {
                // Insert into database (make sure insertTask saves dueAt)
                val newTaskId = dbHelper.insertTask(title, description, dueDate, "todo")

                // Create Task object properly
                val task = Task(
                    id = newTaskId.toString(),
                    userId = null,                  // or set actual user if applicable
                    title = title,
                    description = description,
                    category = null,
                    priority = "1",
                    dueAt = dueDate,                //  this is the selected date
                    status = "todo",                // use lowercase for consistency with adapter
                    createdAt = System.currentTimeMillis().toString()
                )


                taskList.add(task)
                adapter.notifyItemInserted(taskList.size - 1)

                // Clear fields
                etTaskTitle.text.clear()
                etTaskDescription.text.clear()
                tvTaskDate.text = ""
                selectedDate = ""
            } else {
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please select a due date", Toast.LENGTH_SHORT).show()
                }
            }
            // Find the button
            val btnAllTasks = view.findViewById<Button>(R.id.btnAllTasks)

            // Handle navigation
            btnAllTasks.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AllTasksFragment()) // 👈 Use your container ID
                    .addToBackStack(null) // allows back navigation
                    .commit()
            }

        }


        return view
    }




    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date as YYYY-MM-DD
                val date = Calendar.getInstance()
                date.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(date.time)

                // Display formatted date to user
                val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                tvTaskDate.text = displayFormat.format(date.time)
            },
            year,
            month,
            day
        )

        // Optional: Set minimum date to today to prevent selecting past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun loadTasksFromDatabase() {
        val tasksFromDb = dbHelper.getAllTasks()
        taskList.clear()
        taskList.addAll(tasksFromDb)
        adapter.notifyDataSetChanged()
    }
}