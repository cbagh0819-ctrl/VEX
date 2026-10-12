package com.vex.security.scanner

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.vex.security.model.FileMetadata

object FileScanner {

    fun scanFile(
        context: Context,
        uri: Uri,
        source: String
    ): FileMetadata {

        val resolver = context.contentResolver

        val mimeType = FileTypeDetector.getMimeType(context, uri)
        val extension = FileTypeDetector.getExtension(uri)

        var fileName = "Unknown"
        var fileSize = 0L
        var modifiedTime: Long? = null

        resolver.query(
            uri,
            arrayOf(
                OpenableColumns.DISPLAY_NAME,
                OpenableColumns.SIZE
            ),
            null,
            null,
            null
        )?.use { cursor ->

            if (cursor.moveToFirst()) {

                val nameIndex =
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                val sizeIndex =
                    cursor.getColumnIndex(OpenableColumns.SIZE)

                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }

                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    fileSize = cursor.getLong(sizeIndex)
                }
            }
        }

        val extFromName = if (fileName.contains(".")) {
            fileName.substringAfterLast('.', "").lowercase().takeIf { it.isNotBlank() }
        } else null

        val finalExtension = extFromName ?: extension
        val finalMimeType = mimeType
            ?: (finalExtension?.let { android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) })

        val sha256 = resolver.openInputStream(uri)?.use { input ->
            HashCalculator.sha256(input)
        }

        return FileMetadata(
            fileName = fileName,
            filePath = uri.toString(),
            mimeType = finalMimeType,
            extension = finalExtension,
            sizeBytes = fileSize,
            createdTime = null,
            modifiedTime = modifiedTime,
            sha256 = sha256,
            source = source
        )
    }
}
