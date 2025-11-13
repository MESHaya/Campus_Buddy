package com.example.campusbuddy.data

data class Task(
    val id: String,
    val userId: String? = null,
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val priority: String = 1.toString(),
    val dueAt: String? = null,
    var status: String = "todo", // Changed from "Due" to "todo"
    val createdAt: String = System.currentTimeMillis().toString()
)