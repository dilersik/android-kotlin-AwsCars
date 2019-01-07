package com.dilerdesenvolv.carros.domain.service

import android.content.Intent
import android.util.Log
import com.dilerdesenvolv.carros.R
import com.dilerdesenvolv.carros.domain.model.Carro
import com.dilerdesenvolv.carros.utils.NotificationUtils
import com.dilerdesenvolv.carros.views.activity.CarroActivity
import com.dilerdesenvolv.carros.views.activity.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.PendingIntent
import android.support.v4.app.TaskStackBuilder

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseMsg"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived")
        // Check if msg is notification type
        if (remoteMessage.notification != null) {
            this.showNotification(remoteMessage)
        }
        // Check if msg is Data Message
        if (remoteMessage.data.isNotEmpty()) {
            when (remoteMessage.data["type"]) {
                "carro" -> showNotificationCarro(remoteMessage)
                else -> Log.d(TAG, "Invalid msg data type: " + remoteMessage.data["type"])
            }
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, MainActivity::class.java)
        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val msg = remoteMessage.notification?.body!!

        NotificationUtils.create(this, 1, intent, title!!, msg!!)
    }

    private fun showNotificationCarro(remoteMessage: RemoteMessage) {
        val intent = Intent(this, CarroActivity::class.java)
//        intent.putExtras(remoteMessage.data.toBundke())

        val carro = Carro()
        carro.id = remoteMessage.data["id"]?.toLong()
        carro.date_post = remoteMessage.data["date_post"]
        carro.date_update = remoteMessage.data["date_update"]
        carro.tipo = remoteMessage.data["tipo"]
        carro.nome = remoteMessage.data["nome"]
        carro.desc = remoteMessage.data["desc"]
        carro.urlFoto = remoteMessage.data["urlFoto"]
        carro.urlInfo = remoteMessage.data["urlInfo"]
        carro.urlVideo = remoteMessage.data["urlVideo"]
        carro.latitude = remoteMessage.data["latitude"]
        carro.longitude = remoteMessage.data["longitude"]

        intent.putExtra("carro", carro)
        intent.putExtra("isPush", true)

        NotificationUtils.create(this, 1, intent, remoteMessage.data["title"]!!, remoteMessage.data["msg"]!!)
    }

}