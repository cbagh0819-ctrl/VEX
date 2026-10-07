package com.vex.security.metadata

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

data class ExtractedMetadata(
    val name: String,
    val size: Long,
    val mimeType: String?,
    val uri: String
)

object MetadataExtractor {

    fun extract(
        context: Context,
        uri: Uri
    ): ExtractedMetadata {

        var name = "Unknown"
        var size = 0L

        context.contentResolver.query(
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
                    name = cursor.getString(nameIndex)
                }

                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    size = cursor.getLong(sizeIndex)
                }
            }
        }

        return ExtractedMetadata(
            name = name,
            size = size,
            mimeType = context.contentResolver.getType(uri),
            uri = uri.toString()
        )
    }
}
