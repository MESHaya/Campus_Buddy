package com.example.campus_buddy.databse

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.content.ContentValues
import com.example.campusbuddy.data.Task

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL(UserTable.SQL_CREATE)
        db.execSQL(TaskTable.SQL_CREATE)
        db.execSQL(EventTable.SQL_CREATE)
        db.execSQL(ModuleTable.SQL_CREATE)
        db.execSQL(AttendanceTable.SQL_CREATE)
        db.execSQL(BuildingTable.SQL_CREATE)
        db.execSQL(EmergencyContactTable.SQL_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UserTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${TaskTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${EventTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ModuleTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${AttendanceTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${BuildingTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${EmergencyContactTable.TABLE_NAME}")
        onCreate(db)
    }

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
                val password = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COL_PASSWORD))

                android.util.Log.d("DB_DEBUG", "User: id=$id, name=$name,surname=$surname username=$username, email=$email, studentNum=$studentNum")
            } while (cursor.moveToNext())
        } else {
            android.util.Log.d("DB_DEBUG", "No users found")
        }

        cursor.close()
        db.close()
    }

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

    // fun insertTask(title: String, description: String, dueDate: String, s: String) {

   // }
    fun insertUser(name: String, surname: String, username: String, email: String, studentID: String, password: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(UserTable.COL_ID, System.currentTimeMillis().toString()) // unique ID
            put(UserTable.COL_NAME, "$name $surname") // combine if you want
            put(UserTable.COL_USERNAME, username)
            put(UserTable.COL_EMAIL, email)
            put(UserTable.COL_STUDENT_NUM, studentID)
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

    fun updateTaskStatus(id: Any, status: Any) {

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

                // Create Task object
                val task = Task(id, title, description,
                    priority.toString(), dueAt, status, createdAt)
                tasks.add(task)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tasks
    }



    companion object {
        const val DATABASE_NAME = "campusbuddy.db"
        const val DATABASE_VERSION = 2
    }
}
