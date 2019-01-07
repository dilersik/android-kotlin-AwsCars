package com.dilerdesenvolv.carros.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class CameraHelper {

    companion object { private const val TAG = "LOG_CameraHelper" }
    var file: File? = null
        private set

    // Se girou a tela recupera o estado
    fun init(icicle: Bundle?) {
        if (icicle != null) {
            file = icicle.getSerializable("file") as? File
        }
    }

    // Salva o estado
    fun onSaveInstanceState(outState: Bundle) {
        if (file != null) {
            outState.putSerializable("file", file)
        }
    }

    // Intent para abrir a mCamera
    fun open(context: Context, fileName: String): Intent {
        file = getSdCardFile(context, fileName)
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file!!)
        i.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        return i
    }

    private fun getSdCardFile(context: Context, fileName: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return File(dir, fileName)
    }

    // Le o bitmap no tamanho desejado
    fun getBitmap(w: Int, h: Int): Bitmap? {
        file?.apply {
            if (exists()) {
                Log.d(TAG, absolutePath)
                // resize
                val bitmap = ImageUtils.resize(this, w, h)
                Log.d(TAG, ".getBitmap w/h: " + bitmap.width + "/" + bitmap.height)

                return bitmap
            }
        }
        return null
    }

    // Salva o bitmap reduzido no arquivo (para upload)
    fun saveCompress(bitmap: Bitmap) {
        file?.apply {
            Log.d(TAG, ".saveCompress antes (" + bitmap.width + " x " + bitmap.height + ": " + absolutePath)
            val out = FileOutputStream(this)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            Log.d(TAG, ".saveCompress Foto salva (" + bitmap.width + " x " + bitmap.height + "): " + absolutePath)
        }
    }

}