package com.example.campus_buddy.data

data class LocalEvent(
    val id: String,                  // can be DB id or Google event id
    val title: String,
    val date: String,                // yyyy-MM-dd
    val time: String? = null,        // "14:30" etc.
    val description: String? = null,
    val isGoogleEvent: Boolean = false // true if event came from Google Calendar
)
