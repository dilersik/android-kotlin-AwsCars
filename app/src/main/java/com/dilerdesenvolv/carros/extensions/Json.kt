package com.dilerdesenvolv.carros.extensions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

// Converte o obj para JSON
fun Any.toJson(prettyPrinting: Boolean = false): String {
    val builder = GsonBuilder()
    if (prettyPrinting) {
        builder.setPrettyPrinting()
    }
    return builder.create().toJson(this)
}

// Converte json para OBJ
inline fun <reified T> Any.fromJson(json: String): T {
    return Gson().fromJson<T>(json, object: TypeToken<T>() {}.type)
}