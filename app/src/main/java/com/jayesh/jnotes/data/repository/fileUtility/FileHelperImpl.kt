package com.jayesh.jnotes.data.repository.fileUtility

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileHelper {
    override fun getNoteScreenshotDirectory(): File {
        val screenshotDirectory = File(context.cacheDir, "screenshots")
        if (!screenshotDirectory.exists()) {
            screenshotDirectory.mkdirs()
        }
        return screenshotDirectory
    }

    override fun getFileUri(file: File): Uri? {
        return FileProvider.getUriForFile(context, "com.jayesh.jnotes.fileprovider", file)
    }
}