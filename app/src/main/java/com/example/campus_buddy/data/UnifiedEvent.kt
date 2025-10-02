package com.example.campus_buddy

data class UnifiedEvent(
    val id: String,            // String because Google uses string IDs, local uses Int
    val title: String,
    val time: String?,
    val description: String?,
    val isGoogleEvent: Boolean // To know if it’s local or Google
)
