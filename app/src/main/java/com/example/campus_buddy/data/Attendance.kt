package com.example.campus_buddy.data


data class Attendance(
    val id: String,
    val studentId: String,
    val moduleId: String,
    val date: String,
    val status: String // "present", "absent", "late"
)
