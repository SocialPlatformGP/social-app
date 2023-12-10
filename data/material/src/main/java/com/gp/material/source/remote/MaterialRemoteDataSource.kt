package com.gp.material.source.remote

import android.content.Context
import android.net.Uri
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import kotlinx.coroutines.flow.Flow

interface MaterialRemoteDataSource {
    fun uploadFile(fileLocation:String,file:Uri,context: Context)
    fun uploadFolder(fileLocation: String,name:String)
     fun downloadFile(fileLocation: String)
    fun deleteFile(fileLocation: String)
    fun getFileTypeFromName(fileName: String): FileType
    fun uploadMaterialItemToDatabase(materialItem: MaterialItem)

}