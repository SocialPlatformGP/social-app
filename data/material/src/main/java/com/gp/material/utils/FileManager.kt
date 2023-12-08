package com.gp.material.utils

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File

class FileManager(private val context: Context) {

    private lateinit var downloadManager: DownloadManager
    private var downloadID: Long = -1

    fun downloadFile(fileUrl: String, fileName: String, fileType: String) {
        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
        Log.d("TAG", "downloadFile: $fileUrl $fileName")
        val request = DownloadManager.Request(Uri.parse(fileUrl))
        request.setTitle(fileName)
        request.setDescription("File is being downloaded...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val destinationFile = File(storageDir, fileName.replace(" ", "_"))

        val destinationUri = Uri.fromFile(destinationFile)
        request.setDestinationUri(destinationUri)

        val downloadReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                    val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (downloadID == downloadId) {
                        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
                        Log.d("TAGe", "onReceive: $destinationUri $destinationFile")
                        openFileWithMediaStoreUri(destinationUri, context!!, fileType)
                    }
                }
            }
        }

        context.registerReceiver(
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

    @SuppressLint("Range")
    fun openFileWithMediaStoreUri(destinationUri: Uri, context: Context, fileType: String) {
        val contentResolver = context.contentResolver

        val fileName = getFileNameFromUri(destinationUri) ?: ""
        val selection = "${MediaStore.Files.FileColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(fileName)
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            null,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val fileId = it.getLong(it.getColumnIndex(MediaStore.Files.FileColumns._ID))
                val mediaStoreUri = ContentUris.withAppendedId(
                    MediaStore.Files.getContentUri("external"),
                    fileId
                )
                Log.d("TAG", "openFileWithMediaStoreUri: $mediaStoreUri    $fileId")

                // Create an intent to open the file using the obtained MediaStore URI
                val openIntent = Intent(Intent.ACTION_VIEW)
                openIntent.setDataAndType(mediaStoreUri, fileType) // Change MIME type if needed
                openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                try {
                    context.startActivity(openIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context,
                        "No application found to open this file",
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
        }
    }


    private fun getFileNameFromUri(uri: Uri): String? {
        return try {
            val path = uri.path
            path?.substring(path.lastIndexOf('/') + 1)
        } catch (e: Exception) {
            null
        }
    }

}
