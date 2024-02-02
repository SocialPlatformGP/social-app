package com.gp.material.source.remote

import android.content.Context
import android.net.Uri
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow

interface MaterialRemoteDataSource {
    fun uploadFile(fileLocation:String,file:Uri, context: Context): Flow<State<Nothing>>
    fun uploadFolder(fileLocation: String,name:String): Flow<State<Nothing>>
    fun deleteFile(fileLocation: String): Flow<State<Nothing>>
    fun deleteFolder(folderPath:String): Flow<State<Nothing>>
    fun getFileTypeFromName(fileName: String): FileType
    fun uploadMaterialItemToDatabase(materialItem: MaterialItem)
    fun getListOfFiles(path: String): Flow<State<List<MaterialItem>>>

}