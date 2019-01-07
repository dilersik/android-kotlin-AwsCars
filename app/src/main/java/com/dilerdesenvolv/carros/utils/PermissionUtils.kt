package com.dilerdesenvolv.carros.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * Created by dilerdesenvolv on 06/09/2017.
 */
object PermissionUtils {

    fun validate(activity: Activity, requestCode: Int, vararg permissions: String): Boolean {
        val list = ArrayList<String>()
        for (permisssion in permissions) {
            if (ContextCompat.checkSelfPermission(activity, permisssion) != PackageManager.PERMISSION_GRANTED) {
                list.add(permisssion)
            }
        }
        if (list.isEmpty()) {
            return true
        }
        // Lista de permissoes que falta acesso
        val newPermissions = arrayOfNulls<String>(list.size)
        list.toArray(newPermissions)
        // Solicita permissão
        ActivityCompat.requestPermissions(activity, newPermissions, 1)

        return false
    }

}