package com.gp.material.di

import com.google.firebase.storage.FirebaseStorage
import com.gp.material.repository.MaterialRepository
import com.gp.material.repository.MaterialRepositoryImpl
import com.gp.material.source.remote.MaterialRemoteDataSource
import com.gp.material.source.remote.MaterialStorageClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    fun provideMaterialRepository(remoteDataSource: MaterialRemoteDataSource): MaterialRepository
            = MaterialRepositoryImpl(remoteDataSource)
}