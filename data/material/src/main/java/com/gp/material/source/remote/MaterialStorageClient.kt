package com.gp.material.source.remote

import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.gp.material.model.MaterialItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class MaterialStorageClient:MaterialRemoteDataSource{

    val storageRef = FirebaseStorage.getInstance().reference

    override suspend fun uploadFile(fileLocation: String, file: Uri ,context: Context) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()
        val fileName = UUID.randomUUID().toString()
        val path="$fileLocation/$fileName"
        val fName = getFileName(context.contentResolver,file)
        val userEmail = Firebase.auth.currentUser?.email!!
        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("id", fileName)
            .setCustomMetadata("path", path)
            .setCustomMetadata("fName", fName)
            .setCustomMetadata("createdBy", userEmail)
            .build()
        val fileRef: StorageReference = storageRef.child("Materials/$fName")
        fileRef.putFile(file,metadata).addOnSuccessListener {
            progressDialog.cancel()

        }.addOnFailureListener{

        }
    }
   private fun getFileName(contentResolver: ContentResolver, fileUri: Uri): String {
        var fileName: String? = null

        val cursor = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        if (fileName == null) {
            val lastPathSegment = fileUri.lastPathSegment
            if (lastPathSegment != null) {
                fileName = lastPathSegment
            }
        }

        return fileName ?: "unknown_file"
    }
    private fun fileType(fileName:String):String{
        val fileExtension: String = fileName.substringAfterLast('.', "")

        if (fileExtension.equals("jpg", ignoreCase = true) || fileExtension.equals("jpeg", ignoreCase = true)) {
            return "image"
        } else if (fileExtension.equals("pdf", ignoreCase = true)) {
            return "pdf"
        } else {
          return "folder"
        }
    }

    override  suspend fun downloadFile(fileLocation: String) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("<your_bucket>")
        val islandRef = storageRef.child(fileLocation)
        val rootPath = File(Environment.getExternalStorageDirectory(), "Materials")
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }
        val localFile = File(rootPath, "downloads")

        islandRef.getFile(localFile)
            .addOnSuccessListener { taskSnapshot ->
                Log.e("firebase", ";local temp file created $localFile")
            }
            .addOnFailureListener { exception ->
                Log.e("firebase", "local temp file not created $exception")
            }
    }


    override fun deleteFile(fileLocation: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child(fileLocation)

        fileRef.delete().addOnSuccessListener {
            Log.d("funDelete", "delete File success ")
        }.addOnFailureListener {
            Log.d("funDelete", "delete File failed ")
        }
    }

}
