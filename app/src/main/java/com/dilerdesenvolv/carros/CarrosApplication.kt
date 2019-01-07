package com.dilerdesenvolv.carros

import android.app.Application
import android.util.Log

class CarrosApplication : Application() {

    private val TAG = "CarrosApplication"

    // chamado quando Android criar o processo da App
    override fun onCreate() {
        super.onCreate()
        // salva a instancia para acessar como Singleton
        appInstance = this
    }

    companion object {
        // singleton
        private var appInstance : CarrosApplication? = null
        fun getInstance() : CarrosApplication {
            if (appInstance == null) {
                throw IllegalStateException("Configure a class de App no AndroidManifest")
            }
            return appInstance!!
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "CarrosApplication.onTerminate()")
    }

}
