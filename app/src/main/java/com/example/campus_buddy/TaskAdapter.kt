package com.example.campus_buddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusbuddy.data.Task

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onStatusChanged: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
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

        // Display title & description
        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description

        // Set radio buttons based on DB status
        when (task.status) {
            "todo" -> holder.radioDue.isChecked = true
            "done" -> holder.radioDone.isChecked = true
            "inprogress" -> holder.radioOverdue.isChecked = true
        }

        // Handle status changes
        holder.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val newStatus = when (checkedId) {
                R.id.radioDue -> "todo"
                R.id.radioDone -> "done"
                R.id.radioOverdue -> "inprogress"
                else -> task.status
            }

            if (newStatus != task.status) {
                task.status = newStatus
                onStatusChanged(task) // safely update DB
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    // Update the list (for filtering)
    fun updateTasks(newTasks: MutableList<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }
}
