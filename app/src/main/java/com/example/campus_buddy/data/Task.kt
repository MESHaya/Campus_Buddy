
package com.example.campusbuddy.data

    data class Task(
        val id: String,
        val userId: String,
        val title: String,
        val description: String,
        val category: String?,
        val priority: Int,
        val dueAt: String?,
        var status: String, // "todo", "inprogress", "done"
        val createdAt: String
    )

