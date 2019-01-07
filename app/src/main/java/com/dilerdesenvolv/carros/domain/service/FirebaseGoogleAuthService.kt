package com.dilerdesenvolv.carros.domain.service

import android.support.v4.app.FragmentActivity
import com.dilerdesenvolv.carros.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient

object FirebaseGoogleAuthService {

    // Google API Client object.
    private var mGoogleApiClient: GoogleApiClient? = null

    fun googleApiClient(activity: FragmentActivity) : GoogleApiClient? {
        if (mGoogleApiClient == null) {
            // Configure Google Sign In
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.GOOGLE_WEB_CLIENT_ID))
                    .requestEmail()
                    .build()

            // Creating and Configuring Google Api Client.
            mGoogleApiClient = GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity  /* OnConnectionFailedListener */) { }
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build()
        }

        return mGoogleApiClient
    }

}