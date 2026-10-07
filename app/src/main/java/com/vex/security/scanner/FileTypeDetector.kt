package com.vex.security.scanner

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

object FileTypeDetector {

    fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
            ?: getMimeTypeFromExtension(uri)
    }

    private fun getMimeTypeFromExtension(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

        return if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(extension.lowercase())
        } else {
            null
        }
    }

    fun getExtension(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())

        return extension.takeIf { it.isNotEmpty() }?.lowercase()
    }
}
