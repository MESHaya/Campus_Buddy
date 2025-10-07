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
    private var tasks: List<Task>,
    private val onStatusChanged: ((Task) -> Unit)? = null // 👈 made optional
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

        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description

        // Select correct status
        when (task.status) {
            "Due" -> holder.radioDue.isChecked = true
            "Done" -> holder.radioDone.isChecked = true
            "Overdue" -> holder.radioOverdue.isChecked = true
        }

        // Handle user changing status
        holder.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            task.status = when (checkedId) {
                R.id.radioDue -> "Due"
                R.id.radioDone -> "Done"
                R.id.radioOverdue -> "Overdue"
                else -> task.status
            }
            onStatusChanged?.invoke(task) // 👈 safe call
        }
    }

    override fun getItemCount(): Int = tasks.size


    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks.toMutableList()
        notifyDataSetChanged()
    }
}
