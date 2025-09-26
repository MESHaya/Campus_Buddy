package com.example.campus_buddy

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.campus_buddy.databse.DatabaseHelper
import com.example.campus_buddy.databse.TaskTable


class TasksFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var btnSaveTask: Button
    private lateinit var lvTasks: ListView
    private lateinit var adapter: ArrayAdapter<String>
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
        lvTasks = view.findViewById(R.id.lvTasks)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, taskList)
        lvTasks.adapter = adapter

        btnSaveTask.setOnClickListener {
            val title = etTaskTitle.text.toString()
            val description = etTaskDescription.text.toString()

            if (title.isNotEmpty()) {
                dbHelper.insertTask(title, description) // save to DB
                taskList.add("$title - $description")
                adapter.notifyDataSetChanged()

                etTaskTitle.text.clear()
                etTaskDescription.text.clear()
            } else {
                Toast.makeText(requireContext(), "Please enter a task title", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
