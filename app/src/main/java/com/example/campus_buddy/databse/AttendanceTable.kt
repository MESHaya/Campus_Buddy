package com.example.campus_buddy.databse

object AttendanceTable {
    const val TABLE_NAME = "Attendance"
    const val COL_ID = "id"
    const val COL_USER_ID = "userId"
    const val COL_MODULE_ID = "moduleId"
    const val COL_SESSION_AT = "sessionAt"
    const val COL_METHOD = "method"
    const val COL_VALID = "valid"

    const val SQL_CREATE = """
        CREATE TABLE $TABLE_NAME (
            $COL_ID TEXT PRIMARY KEY,
            $COL_USER_ID TEXT,
            $COL_MODULE_ID TEXT,
            $COL_SESSION_AT TIMESTAMP,
            $COL_METHOD TEXT CHECK($COL_METHOD IN ('QR','GPS')),
            $COL_VALID BOOLEAN,
            FOREIGN KEY($COL_USER_ID) REFERENCES User(id),
            FOREIGN KEY($COL_MODULE_ID) REFERENCES Module(id)
        )
    """
}
