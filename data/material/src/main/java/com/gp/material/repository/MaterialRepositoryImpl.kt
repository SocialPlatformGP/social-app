package com.gp.material.repository

import android.content.Context
import android.net.Uri
import com.gp.material.model.FileType
import com.gp.material.model.MaterialItem
import com.gp.material.source.remote.MaterialRemoteDataSource
import com.gp.socialapp.utils.State
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MaterialRepositoryImpl @Inject constructor(private val remoteDataSource: MaterialRemoteDataSource): MaterialRepository{
    override fun getListOfFiles(path: String): Flow<State<List<MaterialItem>>> {
        return remoteDataSource.getListOfFiles(path)
    }

    override fun uploadFile(fileLocation: String, file: Uri, context: Context): Flow<State<Nothing>> {
        return remoteDataSource.uploadFile(fileLocation, file, context)
    }

    override fun uploadFolder(fileLocation: String, name: String): Flow<State<Nothing>> {
        return remoteDataSource.uploadFolder(fileLocation, name)
    }

    override fun deleteFile(fileLocation: String) : Flow<State<Nothing>>{
        return remoteDataSource.deleteFile(fileLocation)
    }

    override fun deleteFolder(folderPath: String) : Flow<State<Nothing>>{
        return remoteDataSource.deleteFolder(folderPath)
    }

    override fun getFileTypeFromName(fileName: String): FileType {
        return remoteDataSource.getFileTypeFromName(fileName)
    }
    override fun uploadMaterialItemToDatabase(materialItem: MaterialItem) {
        return remoteDataSource.uploadMaterialItemToDatabase(materialItem)
    }
}