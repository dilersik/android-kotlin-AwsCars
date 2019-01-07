package com.dilerdesenvolv.carros.extensions

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.dilerdesenvolv.carros.R
import com.squareup.picasso.Picasso
import org.jetbrains.anko.toast

fun ImageView.loadUrl(url: String?, progress: ProgressBar? = null) {
    if (url == null || url.trim().isEmpty()) {
        setImageBitmap(null)
        return
    }
    if (progress == null) {
        Picasso.with(context).load(url).fit().into(this)
    } else {
        progress.visibility = View.VISIBLE
        Picasso.with(context).load(url).fit().into(this,
                object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        progress.visibility = View.GONE
                    }

                    override fun onError() {
                        context.toast(context.getString(R.string.erro_img))
                        progress.visibility = View.GONE
                    }
                })
        
    }
}