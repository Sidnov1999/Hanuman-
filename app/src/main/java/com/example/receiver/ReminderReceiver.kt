package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.DevotionApplication
import com.example.MainActivity
import com.example.util.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "devotion_reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val app = context.applicationContext as DevotionApplication
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val todayStr = sdf.format(Date())
                
                val record = app.repository.getRecordByDate(todayStr)
                val isCompleted = record?.completed == true
                
                if (!isCompleted) {
                    showNotification(context)
                }
                
                // Reschedule for tomorrow to keep the repeating chain bulletproof
                ReminderScheduler.scheduleDailyReminder(context)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create the Notification Channel if Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminder"
            val descriptionText = "Reminder to read Hanuman Chalisa daily"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Persistent notification (ongoing, so they can't swipe it away until they act on it)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Safe fallback drawable
            .setContentTitle("Hanuman Chalisa Reminder")
            .setContentText("Today's prayer is pending. Live the day with positive strength!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Persistent until completed style
            .setAutoCancel(true) // Automatically dismiss when clicked
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
