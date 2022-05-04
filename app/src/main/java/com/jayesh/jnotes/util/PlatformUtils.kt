package com.jayesh.jnotes.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri


object PlatformUtils {
    /**
     * Shares the JPEG image from Uri.
     * @param uri Uri of image to share.
     */
    fun shareImageOnApp(context: Context, uri: Uri, componentName: ComponentName) {
        val intent = prepareJpegImageSharingIntent(uri).apply {
            component = componentName
        }
        context.startActivity(intent)
    }

    fun prepareJpegImageSharingIntent(): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/jpeg"
        }
    }

    private fun prepareJpegImageSharingIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/jpeg"
        }
    }

    fun listAllAppsForMatchingIntent(context: Context, intent: Intent): List<ResolveInfo> {
        return context.packageManager.queryIntentActivities(intent, 0)
            .toList()
            .sortedWith(ResolveInfo.DisplayNameComparator(context.packageManager))
    }
}

