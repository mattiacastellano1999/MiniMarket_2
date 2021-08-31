package com.MCProject.minimarket_1.access.util

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_0.gestor.OrderManagerActivity
import com.MCProject.minimarket_1.MainActivity

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        } else {
            Toast.makeText(context, "Unable to Receive Message: VERSION.SDK_INT < O", Toast.LENGTH_SHORT).show()
        }

    }

    /*
     * Creazione nuova notifica per i gestori
     * Quando cliccata mostra un form di selezione dei rider
     */
    @SuppressLint("WrongConstant")
    fun showGestorNotification(channelID: String, title: String, text: String){
        if(!title.equals("null")) {
            val intent = Intent(context, OrderManagerActivity::class.java).apply {
                flags = com.MCProject.minimarket_1.R.string.SHOWRIDERPOPUP
            }

            val pendingIntent: PendingIntent =
                    PendingIntent
                            .getActivity(context, 0, intent, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notification = Notification.Builder(context, channelID)
                    .setContentTitle("$title: ")
                    .setContentText(text)
                    .setSmallIcon(R.drawable.star_big_on)
                    .setShowWhen(false)
                    .setChannelId(channelID)
                    .setContentIntent(pendingIntent)/*cosa fare quando si preme sulla notifica*/
                    .setAutoCancel(true)
                    .build()
                Log.i("HEY", "showNotification: " + text)
                notificationManager.notify(1, notification)
                MainActivity.frM.deleteFromDB(context, "message_for_"+MainActivity.mail, "/chat")

            } else {
                Toast.makeText(context,
                    "Unable to Receive Message: VERSION.SDK_INT < O",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}