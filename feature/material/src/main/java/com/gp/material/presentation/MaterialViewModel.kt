package com.gp.material.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.material.model.MaterialItem
import com.gp.material.repository.MaterialRepository
import com.gp.socialapp.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialRepo: MaterialRepository
) : ViewModel() {
    private val _actionState: MutableStateFlow<State<String>> = MutableStateFlow(State.Idle)
    val actionState: StateFlow<State<String>> = _actionState.asStateFlow()
    private val _currentPath = MutableStateFlow("materials")
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    val currentPath = _currentPath.asStateFlow()
    private val _items = MutableStateFlow<List<MaterialItem>>(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            _currentPath.collect {
                fetchDataFromFirebaseStorage()
            }
        }
    }

    fun fetchDataFromFirebaseStorage() {
        viewModelScope.launch(Dispatchers.IO) {
            materialRepo.getListOfFiles(_currentPath.value).collect {
                when (it) {
                    is State.SuccessWithData -> {
                        _items.value = emptyList()
                        _isLoading.value = false
                        _actionState.value = State.Idle
                        _items.value = it.data
                    }

                    is State.Error -> {
                        _isLoading.value = false
                        _actionState.value = State.Error("Failed to fetch data from remote host")
                    }

                    is State.Loading -> {
                        _isLoading.value = true
                    }

                    else -> {
                        Log.d("SEERDE", "received other state: ${it.javaClass}")
                    }
                }
            }
        }
    }

    fun openFolder(path: String) {
        _currentPath.value = path
    }

    fun getCurrentPath(): String {
        return _currentPath.value
    }

    fun uploadFile(fileUri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            materialRepo.uploadFile(_currentPath.value, fileUri, context).collect {
                when (it) {
                    is State.Success -> {
                        _isLoading.value = false
                        _actionState.value =
                            State.SuccessWithData("File has been uploaded successfully")
                        fetchDataFromFirebaseStorage()
                    }

                    is State.Error -> {
                        _isLoading.value = false
                        _actionState.value = State.Error("Failed to upload file")
                        Log.d("SEERDE", "uploadFile: error ${it.message}")
                    }

                    is State.Loading -> {
                        _isLoading.value = true
                    }

                    else -> {}
                }
            }
        }
    }

    fun uploadFolder(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            materialRepo.uploadFolder(_currentPath.value, name).collect {
                when (it) {
                    is State.Success -> {
                        _isLoading.value = false
                        _actionState.value =
                            State.SuccessWithData("Folder has been created successfully")
                        fetchDataFromFirebaseStorage()
                    }

                    is State.Error -> {
                        _isLoading.value = false
                        _actionState.value = State.Error("Failed to create folder")
                        Log.d("SEERDE", "uploadFile: error ${it.message}")
                    }

                    is State.Loading -> {
                        _isLoading.value = true
                    }

                    else -> {}
                }
            }

        }
    }

    fun deleteFolder(currentPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            materialRepo.deleteFolder(currentPath).collect {
                when (it) {
                    is State.Success -> {
                        _isLoading.value = false
                        _actionState.value =
                            State.SuccessWithData("Folder has been deleted successfully")
                        fetchDataFromFirebaseStorage()
                    }

                    is State.Error -> {
                        _isLoading.value = false
                        _actionState.value = State.Error("Failed to delete folder")
                        Log.d("SEERDE", "uploadFile: error ${it.message}")
                    }

                    is State.Loading -> {
                        _isLoading.value = true
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteFile(fileLocation: String) {
        viewModelScope.launch(Dispatchers.IO) {
            materialRepo.deleteFile(fileLocation).collect {
                when (it) {
                    is State.Success -> {
                        _isLoading.value = false
                        _actionState.value =
                            State.SuccessWithData("File has been deleted successfully")
                        fetchDataFromFirebaseStorage()
                    }

                    is State.Error -> {
                        _isLoading.value = false
                        _actionState.value = State.Error("Failed to delete file")
                        Log.d("SEERDE", "uploadFile: error ${it.message}")
                    }

                    is State.Loading -> {
                        _isLoading.value = true
                    }

                    else -> {}
                }
            }
        }
    }

    fun goBack(): Boolean {
        val lastSlashIndex = _currentPath.value.lastIndexOf("/")
        return if (lastSlashIndex != 0) {
            _currentPath.value = _currentPath.value.substring(0, lastSlashIndex)
            true
        } else {
            _currentPath.value = "materials"
            false
        }
    }

    fun getCurrentFolderName(path: String): String {
        val lastSlashIndex = path.lastIndexOf("/")
        return if (lastSlashIndex != 0 && lastSlashIndex != -1) {
            path.substring(startIndex = lastSlashIndex + 1)
        } else {
            "Home"
        }
    }
}
