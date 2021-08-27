package com.MCProject.minimarket_1.access.util

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class Notification constructor(val context: Activity) {
    var notificationManager: NotificationManager

    init {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /*
     *   Creazione di un canale di notifica (si possono avere più canali di notifica
     *   es: Sport, News, Politica)
     */
    fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        this.notificationManager.createNotificationChannel(channel)
    }

    /*
     * Creazione nuova notifica
     */
    fun showNotification(channelID: String, title: String, text: String){
        val intent = Intent(context, context::class.java ).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notification = Notification.Builder(context, channelID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_notify_more)
            .setShowWhen(false)
            .setChannelId(channelID)
            .setContentIntent(pendingIntent)/*cosa fare quando si preme sulla notifica*/
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1,notification)
    }
}