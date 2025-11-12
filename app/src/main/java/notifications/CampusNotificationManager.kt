package com.example.campus_buddy.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.campus_buddy.MainActivity
import com.example.campus_buddy.R

class CampusNotificationManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_TASKS = "tasks_channel"
        const val CHANNEL_EVENTS = "events_channel"
        const val CHANNEL_GENERAL = "general_channel"

        const val NOTIFICATION_ID_TASK_ADDED = 1001
        const val NOTIFICATION_ID_TASK_DUE = 1002
        const val NOTIFICATION_ID_EVENT_ADDED = 2001
        const val NOTIFICATION_ID_EVENT_UPCOMING = 2002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Tasks Channel
            val tasksChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Tasks Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for tasks and to-dos"
                enableVibration(true)
            }

            // Events Channel
            val eventsChannel = NotificationChannel(
                CHANNEL_EVENTS,
                "Calendar Events",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for calendar events"
                enableVibration(true)
            }

            // General Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General app notifications"
            }

            notificationManager.createNotificationChannel(tasksChannel)
            notificationManager.createNotificationChannel(eventsChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    /**
     * Show notification when a new task is added
     */
    fun notifyTaskAdded(taskTitle: String, taskDescription: String?) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "tasks")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_TASK_ADDED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Task Added ✅")
            .setContentText(taskTitle)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$taskTitle\n${taskDescription ?: "No description"}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_TASK_ADDED, notification)
    }

    /**
     * Show notification when a task is due today
     */
    fun notifyTaskDueToday(taskTitle: String, dueDate: String) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "tasks")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_TASK_DUE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Due Today! ⏰")
            .setContentText(taskTitle)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$taskTitle is due today: $dueDate"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID_TASK_DUE, notification)
    }

    /**
     * Show notification when a task status changes
     */
    fun notifyTaskStatusChanged(taskTitle: String, newStatus: String) {
        if (!areNotificationsEnabled()) return

        val statusEmoji = when(newStatus) {
            "done" -> "✅"
            "todo" -> "📝"
            "inprogress" -> "⏳"
            else -> "📌"
        }

        val statusText = when(newStatus) {
            "done" -> "completed"
            "todo" -> "marked as to-do"
            "inprogress" -> "marked as in progress"
            else -> "updated"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_TASKS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Task Updated $statusEmoji")
            .setContentText("$taskTitle $statusText")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Show notification when a new event is added
     */
    fun notifyEventAdded(eventTitle: String, eventDate: String, eventTime: String?) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "calendar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_EVENT_ADDED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = if (eventTime.isNullOrEmpty()) "" else " at $eventTime"

        val notification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Event Added 📅")
            .setContentText("$eventTitle on $eventDate$timeText")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_EVENT_ADDED, notification)
    }

    /**
     * Show notification when an event is upcoming (1 hour before)
     */
    fun notifyEventUpcoming(eventTitle: String, eventTime: String) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "calendar")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID_EVENT_UPCOMING,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_EVENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Upcoming Event! 🔔")
            .setContentText("$eventTitle starts at $eventTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(NOTIFICATION_ID_EVENT_UPCOMING, notification)
    }

    /**
     * Show a general notification
     */
    fun showGeneralNotification(title: String, message: String) {
        if (!areNotificationsEnabled()) return

        val notification = NotificationCompat.Builder(context, CHANNEL_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    /**
     * Check if notifications are enabled in settings
     */
    private fun areNotificationsEnabled(): Boolean {
        val prefs = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return prefs.getBoolean("Notifications", true)
    }

    /**
     * Cancel all notifications
     */
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    /**
     * Cancel specific notification
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
}