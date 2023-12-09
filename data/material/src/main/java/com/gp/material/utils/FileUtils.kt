package com.gp.material.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.util.Locale

object FileUtils {
        @SuppressLint("Range")
     fun getFileName(uri: Uri,context: Context): String {
        var fileName = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                fileName = displayName ?: ""
            }
        }
        return fileName
    }

     fun getMimeTypeFromUri(uri: Uri,context: Context): String? {
        val contentResolver: ContentResolver = context.contentResolver
        var mimeType: String? = null

        // Try to query the ContentResolver to get the MIME type
        mimeType = contentResolver.getType(uri)

        if (mimeType == null) {
            // If ContentResolver couldn't determine the MIME type, try getting it from the file extension
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            if (!fileExtension.isNullOrEmpty()) {
                mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(fileExtension.toLowerCase(Locale.US))
            }
        }

        return mimeType
    }
}