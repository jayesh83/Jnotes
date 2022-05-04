package com.jayesh.jnotes.data.repository.fileUtility

import android.net.Uri
import java.io.File

interface FileHelper {
    fun getNoteScreenshotDirectory(): File
    fun getFileUri(file: File): Uri?
}