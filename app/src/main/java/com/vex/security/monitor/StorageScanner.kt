package com.vex.security.monitor

import android.content.Context
import android.net.Uri
import android.os.Environment

object StorageScanner {

    data class StorageFile(
        val name: String,
        val uri: Uri
    )

    fun getRecentFiles(
        context: Context
    ): List<StorageFile> {
        val files = mutableMapOf<String, StorageFile>()

        // 1. Scan Downloads directory (/sdcard/Download) directly
        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (downloadDir.exists() && downloadDir.isDirectory) {
                downloadDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val uri = Uri.fromFile(file)
                        files[file.name] = StorageFile(
                            name = file.name,
                            uri = uri
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Also check external storage root if accessible
        try {
            val externalStorage = Environment.getExternalStorageDirectory()
            if (externalStorage.exists() && externalStorage.isDirectory) {
                externalStorage.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val uri = Uri.fromFile(file)
                        files[file.name] = StorageFile(
                            name = file.name,
                            uri = uri
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 3. Scan app internal cache & files dirs
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val uri = Uri.fromFile(file)
                    files[file.name] = StorageFile(name = file.name, uri = uri)
                }
            }
            context.filesDir.listFiles()?.forEach { file ->
                if (file.isFile) {
                    val uri = Uri.fromFile(file)
                    files[file.name] = StorageFile(name = file.name, uri = uri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return files.values.toList()
    }
}
