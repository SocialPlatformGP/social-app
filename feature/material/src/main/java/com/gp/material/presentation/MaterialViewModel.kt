package com.gp.material.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.component3
import com.google.firebase.storage.ktx.storage
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.material.source.remote.MaterialRemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(private val remoteDataSource: MaterialRemoteDataSource) :
    ViewModel() {

     private var currentPath=""

    private val storageReference: StorageReference = Firebase.storage.reference

    private val _fileItems = MutableStateFlow<List<MaterialItem>>(emptyList())

    private val _folderItems = MutableStateFlow<List<MaterialItem>>(emptyList())
    val folderItems: StateFlow<List<MaterialItem>> get() = _folderItems
    val fileItems: StateFlow<List<MaterialItem>> get() = _fileItems

    fun fetchDataFromFirebaseStorage() {

        val fileItems = mutableListOf<MaterialItem>()

        if (currentPath==""){
            currentPath="materials"
        }
        storageReference.child(currentPath).listAll()
            .addOnSuccessListener { listResult ->
                Log.d("waleed24", listResult.items.toString())
                Log.d("waleed24", listResult.prefixes.toString())
                listResult.items.forEach { item ->
                    item.metadata.addOnSuccessListener { metadata ->
                        val newItem=createMaterialItemFromMetadata(metadata)
                        metadata.reference?.downloadUrl?.addOnSuccessListener { url ->
                            val updatedItem = newItem.copy(fileUrl = url.toString())
                            fileItems.add(updatedItem)
                            if (fileItems.size == listResult.items.size) {
                                _fileItems.value = fileItems
                                Log.d("waleed2", _fileItems.value.toString())
                            }
                        }
                    }
                }
                val folderItems = mutableListOf<MaterialItem>()
                listResult.prefixes.forEach { prefix ->

                    val newItem = createMaterialItemFromPrefix(prefix)
                    folderItems.add(newItem)
                    Log.d("waleed222", _folderItems.value.toString())

                    // Update the StateFlow value when all items are processed
                    if (folderItems.size == listResult.prefixes.size) {
                        _folderItems.value = folderItems
                        Log.d("waleed2", _folderItems.value.toString())
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("waleed2", "Error fetching data: $exception")
            }
    }


    fun openFolder(path: String) {
        currentPath = path
        fetchDataFromFirebaseStorage()
    }


    private fun createMaterialItemFromMetadata(metadata: StorageMetadata): MaterialItem {
        val fileName = metadata.getCustomMetadata("fName") ?: ""
        val path = metadata.getCustomMetadata("path") ?: ""
        val creator = metadata.getCustomMetadata("createdBy") ?: ""
        val id = metadata.getCustomMetadata("id") ?: ""
        val type = metadata.getCustomMetadata("fileType") ?: ""
        val time= metadata.getCustomMetadata("time") ?: ""

        return MaterialItem(
            name = fileName,
            path = path,
            createdBy = creator,
            id = id,
            fileType = stringToFileType(type),
            creationTime=time
        )
    }


    fun clearData(){
        _fileItems.value= emptyList()
        _folderItems.value= emptyList()
    }
    fun getCurrentPath():String{
        return currentPath
    }


    private fun stringToFileType(typeString: String): FileType {
        return try {
            FileType.valueOf(typeString.toUpperCase())
        } catch (e: IllegalArgumentException) {
            FileType.UnKnown
        }
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

    fun uploadFile(fileUri: Uri, context: Context) {
        viewModelScope.launch {
            remoteDataSource.uploadFile(currentPath, fileUri, context)
        }
    }

    fun uploadFolder(currentPath:String,name:String){
        remoteDataSource.uploadFolder(currentPath,name)
    }

    fun deleteFolder(currentPath:String){
        remoteDataSource.deleteFolder(currentPath)
    }

    fun deleteFile(fileLocation: String) {
        remoteDataSource.deleteFile(fileLocation)
    }
    private var onBackPressedCallback: (() -> Unit)? = null


    fun goBack() :Boolean{
        // Find the last index of "/"
        val lastSlashIndex = currentPath.lastIndexOf("/")

        // If there is a "/" in the current path, update to the path without the last folder
        if (lastSlashIndex != -1) {
            currentPath= currentPath.substring(0, lastSlashIndex)
            return true
        } else {
            // If there is no "/", set the current path to the root
            currentPath = "materials"
            return false
        }

        fetchDataFromFirebaseStorage()
    }


}
