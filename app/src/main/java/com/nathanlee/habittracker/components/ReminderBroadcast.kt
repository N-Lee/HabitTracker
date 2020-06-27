package com.nathanlee.habittracker.components

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nathanlee.habittracker.R
import com.nathanlee.habittracker.activities.ShowHabitActivity

class ReminderBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var notificationIntent = Intent(context, ShowHabitActivity::class.java)
        var notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)

        var builder = NotificationCompat.Builder(context, "habitReminder")
            .setSmallIcon(R.drawable.previous_arrow_image)
            .setContentTitle("Habit Tracker")
            .setContentText("Reminder to do stuff")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)

        var notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, builder.build())
    }
}