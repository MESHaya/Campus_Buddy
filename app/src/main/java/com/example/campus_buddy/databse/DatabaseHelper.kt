package com.example.campus_buddy.databse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.campus_buddy.data.LocalEvent
import com.example.campusbuddy.data.Task

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create other tables
        db.execSQL(UserTable.SQL_CREATE)
        db.execSQL(TaskTable.SQL_CREATE)
        db.execSQL(EventTable.SQL_CREATE)
        db.execSQL(ModuleTable.SQL_CREATE)
        db.execSQL(AttendanceTable.SQL_CREATE)
        db.execSQL(BuildingTable.SQL_CREATE)
        db.execSQL(EmergencyContactTable.SQL_CREATE)

        // Calendar events table
        val createEventsTable = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_TIME TEXT,
                $COLUMN_DESCRIPTION TEXT
            );
        """.trimIndent()
        db.execSQL(createEventsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UserTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${TaskTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${EventTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ModuleTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${AttendanceTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${BuildingTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${EmergencyContactTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EVENTS")
        onCreate(db)
    }

    // ----------------- EVENT (CALENDAR) METHODS -----------------
    fun insertEvent(title: String, date: String, time: String?, description: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DATE, date)
            put(COLUMN_TIME, time)
            put(COLUMN_DESCRIPTION, description)
        }
        val result = db.insert(TABLE_EVENTS, null, values)
        db.close()
        return result
    }

    fun getEventsByDate(date: String): List<LocalEvent> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EVENTS, null, "$COLUMN_DATE = ?", arrayOf(date),
            null, null, "$COLUMN_TIME ASC"
        )

        val events = mutableListOf<LocalEvent>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            events.add(LocalEvent(id.toString(), title, date, time, description))
        }
        cursor.close()
        db.close()
        return events
    }

    fun updateEvent(event: LocalEvent): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, event.title)
            put(COLUMN_DATE, event.date)
            put(COLUMN_TIME, event.time)
            put(COLUMN_DESCRIPTION, event.description)
        }
        val result = db.update(TABLE_EVENTS, values, "$COLUMN_ID = ?", arrayOf(event.id.toString()))
        db.close()
        return result
    }

    fun deleteEvent(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_EVENTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    companion object {
        const val DATABASE_NAME = "campusbuddy.db"
        const val DATABASE_VERSION = 4  // bumped because schema changed to LocalEvent

        // Events Table
        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DESCRIPTION = "description"
    }
}
