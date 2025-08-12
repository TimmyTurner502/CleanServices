package com.sjocol.cleanservices.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {
    fun createTempImageUri(context: Context): Uri {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val tempFile = File(imagesDir, "IMG_${timeStamp}.jpg")
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            tempFile
        )
    }
} 