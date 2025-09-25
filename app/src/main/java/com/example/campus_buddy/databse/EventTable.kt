package com.example.campus_buddy.databse

object EventTable {
    const val TABLE_NAME = "Event"
    const val COL_ID = "id"
    const val COL_USER_ID = "userId"
    const val COL_TYPE = "type"
    const val COL_TITLE = "title"
    const val COL_START_AT = "startAt"
    const val COL_END_AT = "endAt"
    const val COL_LOCATION = "location"
    const val COL_MODULE_ID = "moduleId"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_USER_ID TEXT,
            $COL_TYPE TEXT CHECK($COL_TYPE IN ('class','exam','assignment','other')),
            $COL_TITLE TEXT NOT NULL,
            $COL_START_AT TIMESTAMP,
            $COL_END_AT TIMESTAMP,
            $COL_LOCATION TEXT,
            $COL_MODULE_ID TEXT,
            FOREIGN KEY($COL_USER_ID) REFERENCES User(id),
            FOREIGN KEY($COL_MODULE_ID) REFERENCES Module(id)
        )
    """
}
