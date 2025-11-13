package com.example.campus_buddy.databse

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.campus_buddy.Event
import com.example.campus_buddy.notifications.CampusNotificationManager
import com.example.campusbuddy.data.Task
import com.example.campus_buddy.data.User


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
    ): Long {
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

        return result
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

    fun insertAttendance(attendance: com.example.campus_buddy.data.Attendance): Boolean {
        val db = this.writableDatabase

        // Check if attendance for this user + module + date already exists (avoid duplicates)
        val cursor = db.query(
            AttendanceTable.TABLE_NAME,
            arrayOf(AttendanceTable.COL_ID),
            "${AttendanceTable.COL_USER_ID} = ? AND ${AttendanceTable.COL_MODULE_ID} = ? AND ${AttendanceTable.COL_SESSION_AT} = ?",
            arrayOf(attendance.userId, attendance.moduleId, attendance.sessionAt),
            null, null, null
        )

        val alreadyExists = cursor.moveToFirst()
        cursor.close()

        if (alreadyExists) {
            db.close()
            Log.d("DB_ATTENDANCE", "Attendance already marked for ${attendance.userId}")
            return false
        }

        val values = ContentValues().apply {
            put(AttendanceTable.COL_ID, attendance.id)
            put(AttendanceTable.COL_USER_ID, attendance.userId)
            put(AttendanceTable.COL_MODULE_ID, attendance.moduleId)
            put(AttendanceTable.COL_SESSION_AT, attendance.sessionAt)
            put(AttendanceTable.COL_METHOD, attendance.method)
            put(AttendanceTable.COL_VALID, if (attendance.valid) 1 else 0)
        }

        val result = db.insert(AttendanceTable.TABLE_NAME, null, values)
        db.close()

        val success = result != -1L
        Log.d("DB_ATTENDANCE", if (success) "Attendance inserted" else "Failed to insert attendance")
        return success
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

    /**
     * Check if an email already exists in the database
     */
    fun emailExists(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT 1 FROM ${UserTable.TABLE_NAME} WHERE ${UserTable.COL_EMAIL} = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(email))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    /**
     * Check if a username already exists in the database
     */
    fun usernameExists(username: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT 1 FROM ${UserTable.TABLE_NAME} WHERE ${UserTable.COL_USERNAME} = ? LIMIT 1"
        val cursor = db.rawQuery(query, arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


    /**
     * Insert user from Google Sign-In
     * Automatically generates username and password for SSO users
     */
    fun insertGoogleUser(
        googleId: String,
        email: String,
        displayName: String?,
        givenName: String?,
        familyName: String?
    ): Long {
        // Check if user already exists
        if (emailExists(email)) {
            Log.d("DB_DEBUG", "Google user already exists: $email")
            return getUserIdByEmail(email)
        }

        val db = writableDatabase

        // Extract first and last name
        val firstName = givenName ?: displayName?.split(" ")?.firstOrNull() ?: "User"
        val lastName = familyName ?: displayName?.split(" ")?.lastOrNull() ?: ""

        // Generate unique username from email
        val username = email.substringBefore("@")

        // For Google users, we don't need a password (use Google ID as placeholder)
        val placeholderPassword = hashPassword("GOOGLE_SSO_$googleId")

        val values = ContentValues().apply {
            put(UserTable.COL_ID, System.currentTimeMillis().toString())
            put(UserTable.COL_NAME, firstName)
            put(UserTable.COL_SURNAME, lastName)
            put(UserTable.COL_USERNAME, username)
            put(UserTable.COL_EMAIL, email)
            put(UserTable.COL_STUDENT_NUM, "SSO-$googleId")  // Use Google ID as student number
            put(UserTable.COL_PASSWORD, placeholderPassword)  // Not used for SSO
        }

        return try {
            val result = db.insert(UserTable.TABLE_NAME, null, values)

            if (result == -1L) {
                Log.e("DB_ERROR", "Failed to insert Google user")
            } else {
                Log.d("DB_DEBUG", "Google user inserted with rowid=$result")
            }

            result
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error inserting Google user: ${e.message}", e)
            -1L
        } finally {
            db.close()
        }
    }

    /**
     * Get user ID by email (for existing SSO users)
     */
    fun getUserIdByEmail(email: String): Long {
        val db = readableDatabase
        val cursor = db.query(
            UserTable.TABLE_NAME,
            arrayOf(UserTable.COL_ID),
            "${UserTable.COL_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        var userId = -1L
        if (cursor.moveToFirst()) {
            userId = cursor.getString(0).toLongOrNull() ?: -1L
        }

        cursor.close()
        db.close()
        return userId
    }

    /**
     * Check if user signed in with Google (has SSO student number)
     */
    fun isGoogleUser(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            UserTable.TABLE_NAME,
            arrayOf(UserTable.COL_STUDENT_NUM),
            "${UserTable.COL_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        var isSSO = false
        if (cursor.moveToFirst()) {
            val studentNum = cursor.getString(0)
            isSSO = studentNum.startsWith("SSO-")
        }

        cursor.close()
        db.close()
        return isSSO
    }

    /**
     * Get user data by email
     */
    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            UserTable.TABLE_NAME,
            null,
            "${UserTable.COL_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        var user: com.example.campus_buddy.data.User? = null
        if (cursor.moveToFirst()) {
            user = com.example.campus_buddy.data.User(
                id = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_NAME)),
                surname = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_SURNAME)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_USERNAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_EMAIL)),
                studentNum = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_STUDENT_NUM)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PASSWORD))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    // Helper method for hashing (if not already present)
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }
}