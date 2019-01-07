package com.dilerdesenvolv.carros.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.view.View
import com.dilerdesenvolv.carros.R
import org.jetbrains.anko.toast
import java.util.regex.Pattern

object AndroidUtils {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager// 1
        val networkInfo = connectivityManager.activeNetworkInfo // 2
        if (networkInfo != null && networkInfo.isConnected) { // 3
            return true
        }

        return false
    }

    fun showNoNetwork(view: View) {
        val mySnackbar = Snackbar.make(view, view.context.getString(R.string.sem_conexao_verifique), Snackbar.LENGTH_INDEFINITE)
        mySnackbar.setAction(view.context.getString(R.string.ok),
                {
                    try {
                        view.context.startActivity(Intent(Settings.ACTION_SETTINGS))
                    } catch (e: Exception) {
                        view.context.toast(view.context.getString(R.string.as_config_nao))
                    }
                })
        mySnackbar.show()
    }

    fun getYouTubeId(ytUrl: String): String? {
        val compiledPattern = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*")
        val matcher = compiledPattern.matcher(ytUrl)

        if (matcher.find()) {
            return matcher.group()
        }
        return null
    }

    fun openYoutubeLink(context: Context, youtubeID: String) {
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeID))
        val intentBrowser = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + youtubeID))
        try {

            context.startActivity(intentApp)

        } catch (ex: ActivityNotFoundException) {

            context.startActivity(intentBrowser)
        }

    }

}