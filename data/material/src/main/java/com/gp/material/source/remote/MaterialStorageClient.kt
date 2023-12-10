package com.gp.material.source.remote

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.ProgressBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.gp.data.material.R
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject


class MaterialStorageClient @Inject constructor(private val storage: FirebaseStorage):MaterialRemoteDataSource{
    private val imageExtensions = setOf("jpg", "jpeg", "png", "gif")
    private val pdfExtensions = setOf("pdf")
    private val docExtensions = setOf("doc", "docx")
    private val audioExtensions = setOf("mp3", "wav", "ogg")
    private val videoExtensions = setOf("mp4", "avi", "mkv")

    private val storageRef = storage.reference

    override fun uploadFile(currentPath: String, file: Uri, context: Context) {


        val progressDialog = createProgressDialog(context)
        progressDialog.show()

        val fileName = UUID.randomUUID().toString()
        val path = "$currentPath/$fileName"
        val fName = getFileName(context.contentResolver, file)
        val userEmail = Firebase.auth.currentUser?.email!!
        val fileType=getFileTypeFromName(fName)
        val time=System.currentTimeMillis().toString()
        val metadata = createStorageMetadata(fileName, fName, path, userEmail,fileType,time)
        if(currentPath!=""){
            val fileRef: StorageReference = storageRef.child("$currentPath/$fileName")
            uploadFileWithMetadata(fileRef, file, metadata, progressDialog)
        }
        else{
            val fileRef: StorageReference = storageRef.child("materials")
            uploadFileWithMetadata(fileRef, file, metadata, progressDialog)
        }
    }

    private fun createProgressDialog(context: Context): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_dialog_layout) // Replace with your custom layout or use a built-in layout

        val progressBar: ProgressBar = dialog.findViewById(R.id.progressBar)
        progressBar.isIndeterminate = true
        return dialog
    }

    private fun createStorageMetadata(
        id: String,
        fName: String,
        path: String,
        createdBy: String,
        fileType: FileType,
        time:String
        ): StorageMetadata {
        return StorageMetadata.Builder()
            .setCustomMetadata("id", id)
            .setCustomMetadata("fName", fName)
            .setCustomMetadata("path", path)
            .setCustomMetadata("createdBy", createdBy)
            .setCustomMetadata("fileType",fileType.toString() )
            .setCustomMetadata("time", time)
            .build()
    }
    private fun uploadFileWithMetadata(
        fileRef: StorageReference,
        file: Uri,
        metadata: StorageMetadata,
        progressDialog: Dialog
    ) {
        fileRef.putFile(file, metadata)
             .addOnSuccessListener {
                progressDialog.cancel()
            }
            .addOnFailureListener {
                progressDialog.cancel()
            }
    }


    override fun uploadFolder(currentPath: String, name: String) {
        val folderId = UUID.randomUUID().toString()
        val folderPath = "$currentPath/$name/" // Use the provided name for the folder
        val userEmail = Firebase.auth.currentUser?.email!!

        // Create an empty placeholder file for the folder
        val placeholderFileName = ".placeholder" // Use a name that won't interfere with actual files
        val placeholderFilePath = "$folderPath$placeholderFileName"
        val placeholderFileRef: StorageReference = storageRef.child(placeholderFilePath)

        // Set metadata for the placeholder file (can be empty or with minimal information)
        val placeholderMetadata = createFolderMetadata(folderId, name, folderPath, userEmail)

        // Upload the empty placeholder file
        placeholderFileRef.putBytes(ByteArray(0), placeholderMetadata)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
                // Handle upload failure if needed
            }
    }

    private fun createFolderMetadata(id: String, fName: String, path: String, createdBy: String): StorageMetadata {
        return StorageMetadata.Builder()
            .setCustomMetadata("id", id)
            .setCustomMetadata("fName", fName)
            .setCustomMetadata("path", path)
            .setCustomMetadata("createdBy", createdBy)
            .build()
    }


    private fun getFileName(
        contentResolver: ContentResolver
        , fileUri: Uri
        ): String {
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
    override fun getFileTypeFromName(fileName: String): FileType {
        val lowercaseFileName = fileName.toLowerCase()

        // Get the file extension
        val fileExtension = lowercaseFileName.substringAfterLast('.', "")

        // Determine the file type based on the extension
        return when {
            imageExtensions.contains(fileExtension) -> FileType.IMAGE
            pdfExtensions.contains(fileExtension) -> FileType.PDF
            docExtensions.contains(fileExtension) -> FileType.OTHER
            audioExtensions.contains(fileExtension) -> FileType.AUDIO
            videoExtensions.contains(fileExtension) -> FileType.VIDEO
            else -> FileType.FOLDER
        }
    }

    override  fun downloadFile(fileLocation: String) {
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

    override fun uploadMaterialItemToDatabase(materialItem: MaterialItem) {
        // Get a reference to the location where you want to store the MaterialItem object
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val materialItemRef = databaseReference.child("materialItems").child(materialItem.id)

        // Convert the MaterialItem object to a Map
        val materialItemMap = mapOf(
            "name" to materialItem.name,
            "path" to materialItem.path,
            "createdBy" to materialItem.createdBy,
            "id" to materialItem.id,
            "fileType" to materialItem.fileType,
        )
        materialItemRef.setValue(materialItemMap)
            .addOnSuccessListener {
                Log.d("YourActivity", "MaterialItem uploaded successfully") }
            .addOnFailureListener { exception ->
                Log.e("YourActivity", "Error uploading MaterialItem: $exception")
            }
        }
    }
