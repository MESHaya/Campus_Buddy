package com.example.campus_buddy.databse

object ModuleTable {
    const val TABLE_NAME = "Module"
    const val COL_ID = "id"
    const val COL_CODE = "code"
    const val COL_NAME = "name"
    const val COL_LECTURER = "lecturerName"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_CODE TEXT UNIQUE NOT NULL,
            $COL_NAME TEXT NOT NULL,
            $COL_LECTURER TEXT
        )
    """
}
