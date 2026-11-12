package com.vex.security.network

import android.content.Context
import android.net.Uri
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import okio.source
import java.io.IOException

object StreamingMediaUploader {

    /**
     * Chunked streaming file upload for large .mp4 videos and .jpg images.
     * Streams directly from Android ContentResolver to OkHttp socket without loading full bytes into heap.
     * Prevents OutOfMemory (OOM) crashes on 50MB+ uploads.
     */
    fun uploadMediaChunked(
        context: Context,
        uri: Uri,
        url: String,
        mimeType: String = "video/mp4",
        fileName: String = "uploaded_media.mp4",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        val streamingBody = object : RequestBody() {
            override fun contentType(): MediaType? = mimeType.toMediaTypeOrNull()

            override fun writeTo(sink: BufferedSink) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val source = inputStream.source()
                    sink.writeAll(source)
                } ?: throw IOException("Could not open InputStream for URI: $uri")
            }
        }

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", fileName, streamingBody)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(multipartBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message ?: "Upload network failure")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        onSuccess(it.body?.string() ?: "{}")
                    } else {
                        onError("Server rejected upload with HTTP ${it.code}")
                    }
                }
            }
        })
    }
}
