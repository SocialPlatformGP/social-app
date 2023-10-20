package com.gp.users.di

import com.gp.users.Source.remote.UserRemoteDataSource
import com.gp.users.Source.remote.UserfirestoreClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteSourceModule {
    @Provides
    fun provideUserRemoteDataSource(firestore: FirebaseFirestore): UserRemoteDataSource = UserfirestoreClient(firestore)

}