package com.dilerdesenvolv.carros.domain.service

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    private val TAG = "FirebaseID"

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        if (token != null) {
            Log.d(TAG, "onTokenRefresh: " + token)
            sendRegistrationToServer(token)
        }
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO:: Save token in MY server App
    }



}