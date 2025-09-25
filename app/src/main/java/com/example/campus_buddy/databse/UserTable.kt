package com.example.campus_buddy.databse

object UserTable {
    const val TABLE_NAME = "User"
    const val COL_ID = "id"
    const val COL_NAME = "name"
    const val COL_EMAIL = "email"
    const val COL_STUDENT_NUM = "studentNumber"
    const val COL_LANGUAGE = "language"
    const val COL_FACULTY = "faculty"
    const val COL_PASSWORD = "password"
    const val COL_CREATED_AT = "createdAt"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_NAME TEXT NOT NULL,
            $COL_EMAIL TEXT UNIQUE NOT NULL,
            $COL_STUDENT_NUM TEXT,
            $COL_LANGUAGE TEXT,
            $COL_FACULTY TEXT,
            $COL_PASSWORD TEXT NOT NULL,
            $COL_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """
}
