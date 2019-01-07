package com.dilerdesenvolv.carros.extensions

import android.os.Bundle

fun Map<String, String>.toBundke(): Bundle {
    val bundle = Bundle()
    for (key in keys) {
        bundle.putString(key, get(key))
    }
    return bundle
}