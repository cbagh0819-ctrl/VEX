package com.vex.security.monitor

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object FileMonitor {

    private const val WORK_NAME = "VEX_FILE_SCAN"

    fun start(context: Context) {

        val request =
            PeriodicWorkRequestBuilder<FileScanWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager
            .getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }

    fun scanNow(context: Context) {
        val request =
            OneTimeWorkRequestBuilder<FileScanWorker>()
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(request)
    }

    fun stop(context: Context) {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }
}
