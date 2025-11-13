package com.example.campus_buddy.databse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.campus_buddy.Event
import com.example.campus_buddy.notifications.CampusNotificationManager
import com.example.campusbuddy.data.Task

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Initialize notification manager
    private val notificationManager = CampusNotificationManager(context)

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
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

    // ----------------- USER METHODS -----------------
    fun getAllUsersDebug() {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${UserTable.TABLE_NAME}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_NAME))
                val surname = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_SURNAME))
                val username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_USERNAME))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_EMAIL))
                val studentNum = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_STUDENT_NUM))

                Log.d("DB_DEBUG", "User: id=$id, name=$name,$surname username=$username, email=$email, studentNum=$studentNum")
            } while (cursor.moveToNext())
        } else {
            Log.d("DB_DEBUG", "No users found")
        }

        cursor.close()
        db.close()
    }

    fun insertUser(
        name: String,
        surname: String,
        username: String,
        email: String,
        studentID: String,
        password: String
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(UserTable.COL_NAME, name.trim())
            put(UserTable.COL_SURNAME, surname.trim())
            put(UserTable.COL_USERNAME, username.trim())
            put(UserTable.COL_EMAIL, email.trim())
            put(UserTable.COL_STUDENT_NUM, studentID.trim())
            put(UserTable.COL_PASSWORD, password)
        }

        val result = db.insert(UserTable.TABLE_NAME, null, values)
        db.close()

        if (result == -1L) {
            Log.e("DB_ERROR", "Failed to insert user")
        } else {
            Log.d("DB_DEBUG", "User inserted with rowid=$result")
        }
    }

    // ----------------- TASK METHODS WITH NOTIFICATIONS -----------------
    fun insertTask(title: String, description: String, dueAt: String, status: String = "todo"): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(TaskTable.COL_ID, System.currentTimeMillis().toString())
            put(TaskTable.COL_TITLE, title)
            put(TaskTable.COL_DESCRIPTION, description)
            put(TaskTable.COL_DUE_AT, dueAt)
            put(TaskTable.COL_STATUS, status)
        }
        val result = db.insert(TaskTable.TABLE_NAME, null, values)
        db.close()

        // SEND NOTIFICATION when task is added
        if (result != -1L) {
            notificationManager.notifyTaskAdded(title, description)
            Log.d("DB_NOTIFICATION", "Task added notification sent for: $title")
        }

        return result
    }

    fun updateTaskStatus(taskId: String, newStatus: String): Int {
        val db = writableDatabase

        // Get task title for notification
        val cursor = db.query(
            TaskTable.TABLE_NAME,
            arrayOf(TaskTable.COL_TITLE),
            "${TaskTable.COL_ID} = ?",
            arrayOf(taskId),
            null, null, null
        )

        var taskTitle = ""
        if (cursor.moveToFirst()) {
            taskTitle = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_TITLE))
        }
        cursor.close()

        val values = ContentValues().apply {
            put(TaskTable.COL_STATUS, newStatus)
        }
        val result = db.update(
            TaskTable.TABLE_NAME,
            values,
            "${TaskTable.COL_ID} = ?",
            arrayOf(taskId)
        )
        db.close()

        // SEND NOTIFICATION when task status changes
        if (result > 0 && taskTitle.isNotEmpty()) {
            notificationManager.notifyTaskStatusChanged(taskTitle, newStatus)
            Log.d("DB_NOTIFICATION", "Task status changed notification sent for: $taskTitle")
        }

        return result
    }

    fun getTasksByDate(date: String): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(
            TaskTable.TABLE_NAME,
            null,
            "${TaskTable.COL_DUE_AT}=?",
            arrayOf(date),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                tasks.add(
                    Task(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_TITLE)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_DESCRIPTION)),
                        dueAt = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_DUE_AT)),
                        status = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_STATUS))
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tasks
    }

    fun updateTasksStatus(taskId: String, newStatus: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("status", newStatus)
        }
        val rowsAffected = db.update(
            "Task",
            values,
            "id = ?",
            arrayOf(taskId)
        )
        db.close()
        return rowsAffected > 0
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TaskTable.TABLE_NAME}", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_DESCRIPTION))
                val dueAt = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_DUE_AT))
                val priority = cursor.getInt(cursor.getColumnIndexOrThrow(TaskTable.COL_PRIORITY))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_STATUS))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow(TaskTable.COL_CREATED_AT))

                val task = Task(id, title, description, priority.toString(), dueAt, status, createdAt)
                tasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tasks
    }

    // ----------------- EVENT METHODS WITH NOTIFICATIONS -----------------
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

        // SEND NOTIFICATION when event is added
        if (result != -1L) {
            notificationManager.notifyEventAdded(title, date, time)
            Log.d("DB_NOTIFICATION", "Event added notification sent for: $title")
        }

        return result
    }

    fun getEventsByDate(date: String): List<Event> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EVENTS, null, "$COLUMN_DATE = ?", arrayOf(date),
            null, null, "$COLUMN_TIME ASC"
        )

        val events = mutableListOf<Event>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            events.add(Event(id, title, date, time, description))
        }
        cursor.close()
        db.close()
        return events
    }

    fun updateEvent(event: Event): Int {
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

    /**
     * Get tasks due today for notifications
     */
    fun getTasksDueToday(): List<Task> {
        val today = android.text.format.DateFormat.format("yyyy-MM-dd", java.util.Date()).toString()
        return getTasksByDate(today)
    }

    companion object {
        const val DATABASE_NAME = "campusbuddy.db"
        const val DATABASE_VERSION = 3

        // Events Table
        const val TABLE_EVENTS = "events"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DESCRIPTION = "description"
    }
    fun insertAttendance(
        userId: String,
        moduleId: String,
        method: String, // "QR" or "GPS"
        valid: Boolean
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(AttendanceTable.COL_ID, System.currentTimeMillis().toString())
            put(AttendanceTable.COL_USER_ID, userId)
            put(AttendanceTable.COL_MODULE_ID, moduleId)
            put(AttendanceTable.COL_SESSION_AT, System.currentTimeMillis())
            put(AttendanceTable.COL_METHOD, method)
            put(AttendanceTable.COL_VALID, if (valid) 1 else 0)
        }

        val result = db.insert(AttendanceTable.TABLE_NAME, null, values)
        db.close()

        if (result == -1L) {
            Log.e("DB_ERROR", "Failed to insert attendance")
        } else {
            Log.d("DB_DEBUG", "Attendance recorded successfully for user=$userId")
        }

        return result
    }

}