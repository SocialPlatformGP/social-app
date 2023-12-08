package com.gp.material.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.material.source.remote.MaterialRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(private val remoteDataSource: MaterialRemoteDataSource) :
    ViewModel() {

    //VAL CURRENT_PATH
    private val storageReference: StorageReference = Firebase.storage.reference

    private val _fileItems = MutableStateFlow<List<MaterialItem>>(emptyList())
    val fileItems: StateFlow<List<MaterialItem>> get() = _fileItems

    fun fetchDataFromFirebaseStorage(currentPath:String) {
        val fileItems = mutableListOf<MaterialItem>()
        storageReference.child(currentPath).listAll()
            .addOnSuccessListener { listResult ->
                // Process each item asynchronously
                listResult.items.forEach { item ->
                    item.metadata.addOnSuccessListener { metadata ->
                        // Retrieve metadata attributes
                        val fileName = metadata.getCustomMetadata("fName") ?: ""
                        val path = metadata.getCustomMetadata("path") ?: ""
                        val creator = metadata.getCustomMetadata("createdBy") ?: ""
                        val id = metadata.getCustomMetadata("id") ?: ""

                        // Use a temporary item to accumulate data
                        val newItem = MaterialItem(
                            name = fileName,
                            path = path,
                            createdBy = creator,
                            id = id,
                            fileType = getFileType(fileName)
                        )

                        // Retrieve download URL asynchronously
                        metadata.reference?.downloadUrl?.addOnSuccessListener { url ->
                            // Update the URL in the temporary item
                            val updatedItem = newItem.copy(fileUrl = url.toString())

                            // Add the updated item to the list
                            fileItems.add(updatedItem)

                            // Update the StateFlow value when all items are processed
                            if (fileItems.size == listResult.items.size) {
                                _fileItems.value = fileItems
                                Log.d("waleed2", _fileItems.value.toString())
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("waleed2", "Error fetching data: $exception")
            }
    }

    suspend fun uploadFile(fileLocation: String, file: Uri, context: Context) {

        remoteDataSource.uploadFile(fileLocation, file, context)

    }

     fun deleteFile(fileLocation: String) {
        remoteDataSource.deleteFile(fileLocation)
    }

    suspend fun downloadFile(fileLocation: String) {
        remoteDataSource.downloadFile(fileLocation)
    }

    private fun getFileType(fileName: String): FileType {
        val fileExtension = fileName.substringAfterLast('.')
        return when (fileExtension.toLowerCase()) {
            "jpg", "jpeg", "png", "gif" -> FileType.IMAGE
            "pdf" -> FileType.PDF
            "mp3", "wav", "ogg" -> FileType.AUDIO
            "mp4", "avi", "mkv" -> FileType.VIDEO
            else -> FileType.OTHER
        }
    }
}
