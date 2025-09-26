package com.example.campus_buddy.databse

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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

    fun insertTask(title: String, description: String) {

    }
    fun insertUser(name:String,surname:String,username:String,email: String,studentID:String,password:String){

    }

    companion object {
        const val DATABASE_NAME = "campusbuddy.db"
        const val DATABASE_VERSION = 1
    }
}
