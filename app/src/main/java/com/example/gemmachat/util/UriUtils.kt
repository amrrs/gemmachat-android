package com.example.gemmachat.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

fun copyUriToCacheFile(context: Context, uri: Uri, suffix: String? = null): File {
    val extension = suffix ?: inferFileExtension(context, uri)
    val out = File(context.cacheDir, "import_${System.currentTimeMillis()}.$extension")
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(out).use { output -> input.copyTo(output) }
    } ?: error("Could not open $uri")
    return out
}

fun saveBitmapToCacheFile(context: Context, bitmap: Bitmap, suffix: String): File {
    val out = File(context.cacheDir, "capture_${System.currentTimeMillis()}.$suffix")
    FileOutputStream(out).use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
    }
    return out
}

private fun inferFileExtension(context: Context, uri: Uri): String {
    val mimeType = context.contentResolver.getType(uri)?.lowercase()
    val mimeExtension = mimeType?.let { MimeTypeMap.getSingleton().getExtensionFromMimeType(it) }
    if (!mimeExtension.isNullOrBlank()) return mimeExtension

    val pathExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        ?.lowercase()
        ?.substringBefore('?')
        ?.substringBefore('#')
        ?.takeIf { it.isNotBlank() }
    if (!pathExtension.isNullOrBlank()) return pathExtension

    return "jpg"
}
