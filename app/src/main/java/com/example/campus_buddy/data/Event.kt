package com.example.campus_buddy.data

data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val date: String,
    val location: String?,
    val organizer: String?
)
