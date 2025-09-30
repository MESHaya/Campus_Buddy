package com.example.campus_buddy.data


data class Module(
    val id: String,
    val code: String,
    val name: String,
    val lecturer: String?,
    val credits: Int?
)
