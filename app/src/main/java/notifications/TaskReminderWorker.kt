package com.example.campus_buddy.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.campus_buddy.databse.DatabaseHelper

/**
 * Background worker to check for tasks due today and send notifications
 * This runs periodically even when the app is closed
 */
class TaskReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        return try {
            val dbHelper = DatabaseHelper(applicationContext)
            val notificationManager = CampusNotificationManager(applicationContext)

            // Get tasks due today
            val tasksDueToday = dbHelper.getTasksDueToday()

            // Send notification for each task that's not done
            tasksDueToday.forEach { task ->
                if (task.status != "done") {
                    notificationManager.notifyTaskDueToday(
                        task.title,
                        task.dueAt ?: "today"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}