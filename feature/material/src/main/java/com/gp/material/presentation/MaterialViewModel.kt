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

    fun fetchDataFromFirebaseStorage(fileLocation: String) {

        val fileItems = mutableListOf<MaterialItem>()

        storageReference.child(fileLocation).listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    var newItem = MaterialItem()
                    // Retrieve metadata using getMetadata()
                    item.metadata.addOnSuccessListener { metadata ->
                        // Retrieve metadata attributes
                        val fileName = metadata.getCustomMetadata("fName") ?: ""
                        val path = metadata.getCustomMetadata("path") ?: ""
                        val creator = metadata.getCustomMetadata("createdBy") ?: ""
                        val id = metadata.getCustomMetadata("id") ?: ""
                        newItem = newItem.copy(
                            name = fileName,
                            path = path,
                            createdBy = creator,
                            id = id,
                            fileType = getFileType(fileName),
                        )
                        metadata.reference?.downloadUrl?.addOnSuccessListener {
                            newItem = newItem.copy(
                                fileUrl = it.toString()
                            )
                            fileItems.add(newItem)
                            _fileItems.value = fileItems
                            Log.d("waleed2", _fileItems.value.toString())
                        }

                    }
                }
            }
    }

    suspend fun uploadFile(fileLocation: String, file: Uri, context: Context) {

        remoteDataSource.uploadFile(fileLocation, file, context)

    }

    suspend fun deleteFile(fileLocation: String) {
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
