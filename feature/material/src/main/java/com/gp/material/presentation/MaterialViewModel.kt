package com.gp.material.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gp.material.model.MaterialItem
import com.gp.material.repository.MaterialRepository
import com.gp.material.source.remote.MaterialRemoteDataSource
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialRepo: MaterialRepository
     ) : ViewModel() {
    private val _currentPath = MutableStateFlow("materials")
    val currentPath = _currentPath.asStateFlow()
    private val _items = MutableStateFlow<List<MaterialItem>>(emptyList())
    val items = _items.asStateFlow()
    init {
        viewModelScope.launch {
            _currentPath.collect{
                fetchDataFromFirebaseStorage()
            }
        }
        Log.d("SEERDE", "Viewmodel: initial fetch call ")
//        fetchDataFromFirebaseStorage()
    }
    fun fetchDataFromFirebaseStorage() {
        viewModelScope.launch (Dispatchers.IO){
            Log.d("SEERDE", "Viewmodel: fetch call for path: ${_currentPath.value}")
            materialRepo.getListOfFiles(_currentPath.value).collect{
                when(it){
                    is State.SuccessWithData -> {
                        Log.d("SEERDE", "Viewmodel: fetch call success data: ${it.data}")
                        _items.value = it.data
                    }
                    is State.Error -> {
                        Log.d("SEERDE", "fetchDataFromFirebaseStorage: error ${it.message}")
                    }
                    else ->{
                        Log.d("SEERDE", "received other state: ${it.javaClass}")
                    }
                }
            }
        }
    }
    fun openFolder(path: String) {
        _currentPath.value = path
    }
    fun getCurrentPath():String{
        return _currentPath.value
    }
    fun uploadFile(fileUri: Uri, context: Context) {
        Log.d("SEERDE", "uploadFile: called from vm")
        viewModelScope.launch (Dispatchers.IO) {
            materialRepo.uploadFile(_currentPath.value, fileUri, context).collect{
                when(it) {
                    is State.Success -> {
                        //todo stop progress dialog and show snackbar
                        Log.d("SEERDE", "uploadFile: Success in vm")
                        fetchDataFromFirebaseStorage()
                    }
                    is State.Error -> {
                        //todo stop progress dialog and show snackbar
                        Log.d("SEERDE", "uploadFile: error ${it.message}")
                    }
                    is State.Loading -> {
                        //todo show progress dialog
                    }
                    else -> {}
                }
            }
        }
    }
    fun uploadFolder(name:String){
        viewModelScope.launch (Dispatchers.IO) {
            materialRepo.uploadFolder(_currentPath.value, name).collect{
                when(it){
                    is State.Success -> {
                        fetchDataFromFirebaseStorage()
                    }
                    is State.Loading -> {
                        //todo
                    }
                    is State.Error -> {
                        Log.d("SEERDE", "uploadFolder: error :${it.message}")
                    }
                    else -> {}
                }
            }

        }
    }
    fun deleteFolder(currentPath:String){
        viewModelScope.launch (Dispatchers.IO) {
            materialRepo.deleteFolder(currentPath).collect{
                when(it){
                    is State.Success -> {
                        fetchDataFromFirebaseStorage()
                    }
                    is State.Loading -> {
                        //todo
                    }
                    is State.Error -> {
                        Log.d("SEERDE", "uploadFolder: error :${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }
    fun deleteFile(fileLocation: String) {
        viewModelScope.launch (Dispatchers.IO) {
            materialRepo.deleteFile(fileLocation).collect{
                when(it){
                    is State.Success -> {
                        fetchDataFromFirebaseStorage()
                    }
                    is State.Loading -> {
                        //todo
                    }
                    is State.Error -> {
                        Log.d("SEERDE", "uploadFolder: error :${it.message}")
                    }
                    else -> {}
                }
            }
        }
    }
    fun goBack() :Boolean{
        val lastSlashIndex = _currentPath.value.lastIndexOf("/")
        return if (lastSlashIndex != 0) {
            _currentPath.value = _currentPath.value.substring(0, lastSlashIndex)
            true
        } else {
            _currentPath.value = "materials"
            false
        }
    }
    fun getCurrentFolderName(path: String) :String{
        val lastSlashIndex = path.lastIndexOf("/")
        return if (lastSlashIndex != 0 && lastSlashIndex != -1) {
            path.substring(startIndex = lastSlashIndex+1)
        } else {
            "Home"
        }
    }
}
