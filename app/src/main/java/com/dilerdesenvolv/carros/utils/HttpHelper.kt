package com.dilerdesenvolv.carros.utils

import android.util.Log
import okhttp3.*
import java.io.IOException

object HttpHelper {
    
    private const val TAG = "LOG_HttpHelper"
    private const val LOG_ON = true
    private val JSON = MediaType.parse("application/json; charset=utf-8")
    private var client = OkHttpClient()
    
    // get Json
    fun get(url: String): String {
        log(".get: $url")
        return getJson(Request.Builder().url(url).get().build())
    }

    // post Json
    fun post(url: String, json: String): String {
        log(".post: $url > $json")
        val body = RequestBody.create(JSON, json)
        return getJson(Request.Builder().url(url).post(body).build())
    }
    
    // post com parametros (form-urlencoded)
    fun postForm(url: String, params: Map<String, String>): String {
        log(".postForm: $url > $params")
        val builder = FormBody.Builder()
        for ((key, value) in params) {
            builder.add(key, value)
        }
        val body = builder.build()
        // Faz a request
        return getJson(Request.Builder().url(url).post(body).build())
    }
    
    fun delete(url: String): String {
        log(".delete: $url")
        return getJson(Request.Builder().url(url).delete().build())
    }
    
    // Le a resposta do server Json
    private fun getJson(request: Request): String {
        val responseBody = client.newCall(request).execute().body()
        if (responseBody != null) {
            val json = responseBody.string()
            log(" << : $json")
            return json
        }
        throw IOException("Erro ao fazer a requisição")
    }
    
    private fun log(s: String) {
        if (LOG_ON) {
            Log.d(TAG, s);
        }
    }
    
}