package com.vex.security.scanner

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri

object ApkAnalyzer {

    fun getPermissions(
        context: Context,
        apkUri: Uri
    ): List<String> {

        val packageManager = context.packageManager

        val packageInfo: PackageInfo =
            packageManager.getPackageArchiveInfo(
                apkUri.toString(),
                PackageManager.GET_PERMISSIONS
            ) ?: return emptyList()

        return packageInfo.requestedPermissions?.toList()
            ?: emptyList()
    }
}
