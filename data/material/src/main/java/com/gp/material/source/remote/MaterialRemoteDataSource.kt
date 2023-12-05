package com.gp.material.source.remote

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface MaterialRemoteDataSource {
    suspend fun uploadFile(fileLocation:String,file:Uri,context: Context)
    suspend  fun downloadFile(fileLocation: String)
    fun deleteFile(fileLocation: String)

}