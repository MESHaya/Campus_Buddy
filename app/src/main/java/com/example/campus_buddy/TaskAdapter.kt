package com.example.campus_buddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusbuddy.data.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onStatusChanged: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroupStatus)
        val radioDue: RadioButton = itemView.findViewById(R.id.radioDue)
        val radioDone: RadioButton = itemView.findViewById(R.id.radioDone)
        val radioOverdue: RadioButton = itemView.findViewById(R.id.radioOverdue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Display basic info
        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description ?: ""
        holder.tvDueDate.text = formatDueDate(task.dueAt)

        // Prevent triggering listener when programmatically checking buttons
        holder.radioGroup.setOnCheckedChangeListener(null)

        //  Map status to correct radio button
        when (task.status.lowercase()) {
            "due" -> holder.radioDue.isChecked = true
            "done" -> holder.radioDone.isChecked = true
            "overdue" -> holder.radioOverdue.isChecked = true
            else -> holder.radioDue.isChecked = true // default
        }

        //  Listen for user changes and update the task
        holder.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newStatus = when (checkedId) {
                R.id.radioDue -> "due"
                R.id.radioDone -> "done"
                R.id.radioOverdue -> "overdue"
                else -> task.status
            }

            if (newStatus != task.status) {
                task.status = newStatus
                onStatusChanged(task) // callback to fragment to update DB
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    private fun formatDueDate(dueAt: String?): String {
        return try {
            if (dueAt.isNullOrBlank()) return "No due date"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dueAt)
            "Due: ${outputFormat.format(date ?: Date())}"
        } catch (e: Exception) {
            "Due: $dueAt"
        }
    }

    fun addTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
