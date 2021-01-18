package com.lodbrock.tasker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.lodbrock.tasker.App.Companion.CHANNEL_1_ID

class AlertReceiver : BroadcastReceiver() {
    lateinit var notificationManager : NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {

        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_done)
            .setContentTitle("Notification")
            .setContentText("Notification content")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        notificationManager.notify(1, notification)
    }
}