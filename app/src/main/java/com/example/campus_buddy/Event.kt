package com.example.campus_buddy

data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val time: String?,
    val description: String?
)
