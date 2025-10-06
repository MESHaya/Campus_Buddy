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
import java.util.Calendar

class TasksFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var tvTaskDate: TextView
    private lateinit var btnSaveTask: Button
    private lateinit var recyclerTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

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

        taskList.add(Task("1", "Sample Task", "This is a test", "High", "2025-10-01", "Due", "2025-09-30"))
        adapter.notifyDataSetChanged()

        val tvTaskDateLabel = view.findViewById<TextView>(R.id.tvTaskDateLabel)

        tvTaskDateLabel.setOnClickListener {
            // Get current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create and show date picker dialog
            val datePicker = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // When user selects a date
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    tvTaskDateLabel.text = selectedDate
                },
                year, month, day
            )

            datePicker.show()
        }


        // Load tasks from DB on start
        loadTasksFromDatabase()

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString()
            val description = etTaskDescription.text.toString()
            val dueDate = tvTaskDate.text.toString()

            if (title.isNotEmpty()) {
                // Save to DB
                val newTaskId = dbHelper.insertTask(title, description, dueDate, "Due")

                // Create task object and add to list
                val task = Task(newTaskId.toString(), title, description, dueDate, "Due")
                taskList.add(task)
                adapter.notifyItemInserted(taskList.size - 1)

                // Clear fields
                etTaskTitle.text.clear()
                etTaskDescription.text.clear()
                tvTaskDate.text = ""
            } else {
                Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }



    private fun loadTasksFromDatabase() {
        val tasksFromDb = dbHelper.getAllTasks()
        taskList.clear()
        taskList.addAll(tasksFromDb)
        adapter.notifyDataSetChanged()
    }
}
