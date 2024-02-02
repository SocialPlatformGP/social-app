package com.gp.material.source.remote

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.tasks.Tasks
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
import com.gp.material.utils.FileExtensionSets.Companion.getFileType
import com.gp.material.utils.FileUtils.getFileSizeString
import com.gp.socialapp.utils.State
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject


class MaterialStorageClient @Inject constructor(private val storage: FirebaseStorage):MaterialRemoteDataSource{
    private val storageRef = storage.reference

    override fun uploadFile(fileLocation: String, file: Uri, context: Context) :Flow<State<Nothing>> = callbackFlow {
        trySend(State.Loading)
        Log.d("SEERDE", "uploadFile: call received in client")
        val fileName = UUID.randomUUID().toString()
        val path = "$fileLocation/$fileName"
        val fName = getFileName(context.contentResolver, file)
        val userEmail = Firebase.auth.currentUser?.email!!
        val fileType=getFileTypeFromName(fName)
        val time=System.currentTimeMillis().toString()
        val metadata = createStorageMetadata(fileName, fName, path, userEmail,fileType,time)
        val fileRef: StorageReference = storageRef.child("$fileLocation/$fileName")
        fileRef.putFile(file,metadata).addOnSuccessListener {
            Log.d("SEERDE", "uploadFile: success")
            trySend(State.Success)
        }.addOnFailureListener {
            Log.d("SEERDE", "uploadFile: failed ${it.message}")
            trySend(State.Error(it.message!!))
        }
        awaitClose {}
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
    ) {
        fileRef.putFile(file, metadata)
    }


    override fun uploadFolder(currentPath: String, name: String): Flow<State<Nothing>> = callbackFlow {
        trySend(State.Loading)
        val folderId = UUID.randomUUID().toString()
        val folderPath = "$currentPath/$name/"
        val userEmail = Firebase.auth.currentUser?.email!!
        val placeholderFileName = "...placeholder"
        val placeholderFilePath = "$folderPath$placeholderFileName"
        val placeholderFileRef: StorageReference = storageRef.child(placeholderFilePath)
        val placeholderMetadata = createFolderMetadata(folderId, name, folderPath, userEmail)

        placeholderFileRef.putBytes(ByteArray(0), placeholderMetadata)
            .addOnSuccessListener {
                trySend(State.Success)
            }
            .addOnFailureListener {
                trySend(State.Error(it.message!!))
            }
        awaitClose()
    }

    private fun createFolderMetadata(id: String, fName: String, path: String, createdBy: String): StorageMetadata {
        return StorageMetadata.Builder()
            .setCustomMetadata("id", id)
            .setCustomMetadata("fName", fName)
            .setCustomMetadata("path", path)
            .setCustomMetadata("createdBy", createdBy)
            .build()
    }
    override fun deleteFolder(folderPath: String): Flow<State<Nothing>> = callbackFlow{
        trySend(State.Loading)
        val folderRef: StorageReference = storageRef.child(folderPath)
        folderRef.listAll()
            .addOnSuccessListener { listResult ->
                val deleteFileTasks = listResult.items.map { it.delete() }
                val deleteFolderTask = folderRef.delete()
                Tasks.whenAllComplete(deleteFileTasks + deleteFolderTask)
                    .addOnSuccessListener {
                        trySend(State.Success)
                    }
                    .addOnFailureListener { exception ->
                        trySend(State.Error(exception.message!!))
                    }
            }
            .addOnFailureListener { exception ->
                trySend(State.Error(exception.message!!))
            }
        awaitClose()
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
        val lowercaseFileName = fileName.lowercase()

        val fileExtension = lowercaseFileName.substringAfterLast('.', "")
        return getFileType(fileExtension)
    }



    override fun deleteFile(fileLocation: String) : Flow<State<Nothing>> = callbackFlow{
        trySend(State.Loading)
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child(fileLocation)
        fileRef.delete().addOnSuccessListener {
            Log.d("funDelete", "delete File success ")
            trySend(State.Success)
        }.addOnFailureListener {
            Log.d("funDelete", "delete File failed ")
            trySend(State.Error(it.message!!))
        }
        awaitClose()
    }

    override fun uploadMaterialItemToDatabase(materialItem: MaterialItem) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val materialItemRef = databaseReference.child("materialItems").child(materialItem.id)
        val materialItemMap = mapOf(
            "name" to materialItem.name
        )
        materialItemRef.setValue(materialItemMap)
            .addOnSuccessListener {
                Log.d("YourActivity", "MaterialItem uploaded successfully") }
            .addOnFailureListener { exception ->
                Log.e("YourActivity", "Error uploading MaterialItem: $exception")
            }
        }

    override fun getListOfFiles(path: String): Flow<State<List<MaterialItem>>> = callbackFlow {
        trySend(State.Loading)
        val result = mutableListOf<MaterialItem>()
        val folders = mutableListOf<MaterialItem>()
        val files = mutableListOf<MaterialItem>()
        storageRef.child(path).listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    val newItem = createMaterialItemFromPrefix(prefix)
                    folders.add(newItem)
                }
                result.addAll(folders.sortedBy { it.name })
                listResult.items.forEach { item ->
                    item.metadata.addOnSuccessListener { metadata ->
                        val newItem=createMaterialItemFromMetadata(metadata)
                        metadata.reference?.downloadUrl?.addOnSuccessListener { url ->
                            val updatedItem = newItem.copy(fileUrl = url.toString())
                            files.add(updatedItem)
                            Log.d("SEERDE", "getListOfFiles: item: ${updatedItem.name}, ${files.size}")
                            if(files.size == listResult.items.size){
                                result.addAll(files.sortedBy { it.name })
                                trySend(State.SuccessWithData(result))
                            }
                        }
                    }
                }
                result.addAll(files.sortedBy { it.name })
                Log.d("SEERDE", "getListOfFiles: ${result.size} - ${listResult.items.size + listResult.prefixes.size}")
                Log.d("SEERDE", "getListOfFiles: $result")
                if(result.size == listResult.items.size + listResult.prefixes.size){
                    trySend(State.SuccessWithData(result))
                }
            }
            .addOnFailureListener { exception ->
                trySend(State.Error("${exception.message}"))
                Log.e("waleed2", "Error fetching data: $exception")
            }
        awaitClose()
    }
    private fun createMaterialItemFromMetadata(metadata: StorageMetadata): MaterialItem {
        val fileName = metadata.getCustomMetadata("fName") ?: ""
        val path = metadata.getCustomMetadata("path") ?: ""
        val creator = metadata.getCustomMetadata("createdBy") ?: ""
        val id = metadata.getCustomMetadata("id") ?: ""
        val type = metadata.getCustomMetadata("fileType") ?: ""
        val time= metadata.getCustomMetadata("time") ?: ""
        val size = metadata.sizeBytes
        val updatedTime = metadata.updatedTimeMillis

        return MaterialItem(
            name = fileName,
            path = path,
            createdBy = creator,
            id = id,
            fileType = stringToFileType(type),
            creationTime=time,
            size = getFileSizeString(size)
        )
    }
    private fun createMaterialItemFromPrefix(prefix: StorageReference): MaterialItem {
        val fileName = prefix.name ?: ""
        val path = prefix.path ?: ""
        return MaterialItem(
            name = fileName,
            path = path,
            fileType = FileType.FOLDER
        )
    }
    private fun stringToFileType(typeString: String): FileType {
        return try {
            FileType.valueOf(typeString.uppercase())
        } catch (e: IllegalArgumentException) {
            FileType.UnKnown
        }
    }

}
