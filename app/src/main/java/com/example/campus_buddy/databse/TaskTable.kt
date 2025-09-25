package com.example.campus_buddy.databse

object TaskTable {
    const val TABLE_NAME = "Task"
    const val COL_ID = "id"
    const val COL_USER_ID = "userId"
    const val COL_TITLE = "title"
    const val COL_DESCRIPTION = "description"
    const val COL_CATEGORY = "category"
    const val COL_PRIORITY = "priority"
    const val COL_DUE_AT = "dueAt"
    const val COL_STATUS = "status"
    const val COL_CREATED_AT = "createdAt"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_USER_ID TEXT,
            $COL_TITLE TEXT NOT NULL,
            $COL_DESCRIPTION TEXT,
            $COL_CATEGORY TEXT,
            $COL_PRIORITY INTEGER CHECK($COL_PRIORITY BETWEEN 1 AND 3),
            $COL_DUE_AT TIMESTAMP,
            $COL_STATUS TEXT CHECK($COL_STATUS IN ('todo','inprogress','done')),
            $COL_CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY($COL_USER_ID) REFERENCES User(id)
        )
    """
}
