package com.example.campus_buddy.databse

object BuildingTable {
    const val TABLE_NAME = "Building"
    const val COL_ID = "id"
    const val COL_NAME = "name"
    const val COL_LAT = "lat"
    const val COL_LNG = "lng"
    const val COL_TYPE = "type"
    const val COL_PHONE = "phone"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_NAME TEXT NOT NULL,
            $COL_LAT REAL,
            $COL_LNG REAL,
            $COL_TYPE TEXT,
            $COL_PHONE TEXT
        )
    """
}
