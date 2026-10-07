package com.vex.security.model

data class FileMetadata(
    val fileName: String,
    val filePath: String,
    val mimeType: String?,
    val extension: String?,
    val sizeBytes: Long,
    val createdTime: Long?,
    val modifiedTime: Long?,
    val sha256: String?,
    val source: String
)
