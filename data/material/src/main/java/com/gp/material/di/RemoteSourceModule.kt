package com.gp.material.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.gp.material.source.remote.MaterialRemoteDataSource
import com.gp.material.source.remote.MaterialStorageClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RemoteSourceModule {
    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage
    @Provides
    fun provideMaterialRemoteDataSource(storage: FirebaseStorage): MaterialRemoteDataSource
            = MaterialStorageClient(storage)
}