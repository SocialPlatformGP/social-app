// FileViewModel.kt
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.material.source.remote.MaterialRemoteDataSource
import dagger.hilt.android.ViewModelLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MaterialViewModel(val remoteDataSource: MaterialRemoteDataSource) : ViewModel() {

    private val storage = Firebase.storage
    private val storageReference: StorageReference = storage.reference

    private val _fileItems = MutableStateFlow<List<MaterialItem>>(emptyList())
    val fileItems: StateFlow<List<MaterialItem>> get() = _fileItems

    fun fetchDataFromFirebaseStorage(fileLocation: String) {

        val fileItems = mutableListOf<MaterialItem>()

        storageReference.child(fileLocation).listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    // Retrieve metadata using getMetadata()
                    item.metadata.addOnSuccessListener { metadata ->
                        // Retrieve metadata attributes
                        val fileName = metadata.getCustomMetadata("fName") ?: ""
                        val path = metadata.getCustomMetadata("path") ?: ""
                        val creator = metadata.getCustomMetadata("createdBy") ?: ""
                        val id = metadata.getCustomMetadata("id") ?: ""
                        val fileItem = MaterialItem(
                            id=id,
                            name = fileName,
                            fileUrl = item.downloadUrl.toString(),
                            fileType = getFileType(item.name),
                            path = path,
                            createdBy = creator
                        )
                        Log.d("lofg", fileItem.toString())

                        fileItems.add(fileItem)

                        _fileItems.value = fileItems
                    }
                }
            }
    }
    suspend fun uploadFile(fileLocation:String, file: Uri, context: Context){

        remoteDataSource.uploadFile(fileLocation,file,context)

    }
    suspend fun deleteFile(fileLocation: String){
        remoteDataSource.deleteFile(fileLocation)
    }
    suspend fun downloadFile(fileLocation: String){
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
