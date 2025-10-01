
package com.example.campusbuddy.data

data class Task(
    val id: String,
    val userId: String? = null,
    val title: String,
    val description: String? = null,
    val category: String? = null,
    val priority: String = 1.toString(),  // default priority
    val dueAt: String? = null,
    var status: String = "Due", // default status
    val createdAt: String = System.currentTimeMillis().toString() // default timestamp
)

