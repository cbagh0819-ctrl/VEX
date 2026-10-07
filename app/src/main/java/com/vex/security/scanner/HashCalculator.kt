package com.vex.security.scanner

import java.io.InputStream
import java.security.MessageDigest

object HashCalculator {

    fun sha256(inputStream: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)

        inputStream.use { input ->
            var bytesRead: Int

            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }

        return digest.digest().joinToString("") {
            "%02x".format(it)
        }
    }
}
