package com.vex.security.monitor

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

object StorageScanner {

    data class StorageFile(
        val name: String,
        val uri: Uri
    )

    fun getRecentFiles(
        context: Context
    ): List<StorageFile> {

        val files = mutableListOf<StorageFile>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME
        )

        val uri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
        )?.use { cursor ->

            val idIndex =
                cursor.getColumnIndex(
                    MediaStore.Files.FileColumns._ID
                )

            val nameIndex =
                cursor.getColumnIndex(
                    MediaStore.Files.FileColumns.DISPLAY_NAME
                )

            while (cursor.moveToNext() && files.size < 20) {

                if (idIndex >= 0 && nameIndex >= 0) {

                    val id = cursor.getLong(idIndex)

                    val name = cursor.getString(nameIndex)

                    val fileUri =
                        Uri.withAppendedPath(
                            uri,
                            id.toString()
                        )

                    files.add(
                        StorageFile(
                            name = name,
                            uri = fileUri
                        )
                    )
                }
            }
        }

        return files
    }
}
