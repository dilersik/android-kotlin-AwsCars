package com.dilerdesenvolv.carros.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import com.dilerdesenvolv.carros.R

object NotificationUtils {

    internal val CHANNEL_ID = "1"

    fun create(context: Context, id: Int, intent: Intent, title: String, text: String) {
        this.createChannel(context)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Intent para disparar o broadcast
        val p = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // Cria a notificação
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(p)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
        // Dispara a notificação
        manager.notify(id, builder.build())
    }

    // Registra o canal (channel)
    private fun createChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val c = NotificationChannel(CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT)
            c.lightColor = Color.BLUE
            c.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            manager.createNotificationChannel(c)
        }
    }

}