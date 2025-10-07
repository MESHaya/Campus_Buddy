package com.example.campus_buddy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campus_buddy.databse.DatabaseHelper
import com.example.campusbuddy.data.Task

class AllTasksFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var spinnerFilter: Spinner
    private var allTasks: List<Task> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_tasks, container, false)

        dbHelper = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recyclerAllTasks)
        spinnerFilter = view.findViewById(R.id.spinnerFilter)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load all tasks
        allTasks = dbHelper.getAllTasks()
        taskAdapter = TaskAdapter(allTasks)
        recyclerView.adapter = taskAdapter

        setupFilterSpinner()

        return view
    }

    private fun setupFilterSpinner() {
        val options = listOf("All", "Due", "Done", "Overdue")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = options[position]
                filterTasks(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterTasks(status: String) {
        val filteredTasks = when (status) {
            "Due" -> allTasks.filter { it.status.equals("Due", true) }
            "Done" -> allTasks.filter { it.status.equals("Done", true) }
            "Overdue" -> allTasks.filter { it.status.equals("Overdue", true) }
            else -> allTasks
        }
        taskAdapter.updateTasks(filteredTasks)
    }
}
