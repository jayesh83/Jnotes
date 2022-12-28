package com.jayesh.jnotes.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult


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

    fun shareImageOnApp(uri: Uri, componentName: ComponentName, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val intent = prepareJpegImageSharingIntent(uri).apply {
            component = componentName
        }
        launcher.launch(intent)
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

    fun TextView.afterTextChangedDelayed(afterTextChanged: (String, Int) -> Unit) {
        addTextChangedListener(
            object : TextWatcher {
                var timer: CountDownTimer? = null

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    timer?.cancel()
                    timer = object : CountDownTimer(500, 1500) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            if (hasFocus()) {
                                afterTextChanged(editable?.toString() ?: "", 1)
                            }
                        }
                    }.start()
                }
            }
        )
    }
}

