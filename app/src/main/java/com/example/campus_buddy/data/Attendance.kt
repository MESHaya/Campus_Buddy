package com.example.campus_buddy.data


data class Attendance(
    val id: String,
    val userId: String,
    val moduleId: String,
    val sessionAt: String,
    val method: String,
    val valid: Boolean
)