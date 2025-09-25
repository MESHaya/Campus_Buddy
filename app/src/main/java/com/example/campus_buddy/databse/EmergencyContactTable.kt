package com.example.campus_buddy.databse

object EmergencyContactTable {
    const val TABLE_NAME = "EmergencyContact"
    const val COL_ID = "id"
    const val COL_NAME = "name"
    const val COL_PHONE = "phone"
    const val COL_TYPE = "type"
    const val COL_PRIORITY = "priority"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_NAME TEXT NOT NULL,
            $COL_PHONE TEXT NOT NULL,
            $COL_TYPE TEXT CHECK($COL_TYPE IN ('security','ambulance','police','fire','counseling')),
            $COL_PRIORITY INTEGER
        )
    """
}
