package com.example.campus_buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campus_buddy.databse.DatabaseHelper

class TasksFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var btnSaveTask: Button
    private lateinit var recyclerTasks: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tasks, container, false)

        dbHelper = DatabaseHelper(requireContext())

        etTaskTitle = view.findViewById(R.id.etTaskTitle)
        etTaskDescription = view.findViewById(R.id.etTaskDescription)
        btnSaveTask = view.findViewById(R.id.btnSaveTask)
        recyclerTasks = view.findViewById(R.id.recyclerTasks)

        // Setup RecyclerView
        adapter = TaskAdapter(taskList)
        recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        recyclerTasks.adapter = adapter

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString()
            val description = etTaskDescription.text.toString()

            if (title.isNotEmpty()) {
                // Save to DB
                dbHelper.insertTask(title, description)

                // Update RecyclerView
                taskList.add("$title - $description")
                adapter.notifyItemInserted(taskList.size - 1)

                etTaskTitle.text.clear()
                etTaskDescription.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
