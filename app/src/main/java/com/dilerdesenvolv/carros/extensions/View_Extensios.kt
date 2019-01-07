package com.dilerdesenvolv.carros.extensions

fun android.view.View.onClick(l: (v: android.view.View?) -> Unit) {
    setOnClickListener(l)
}