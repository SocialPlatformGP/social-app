package com.example.users.di

import com.example.users.Source.remote.UserRemoteDataSource
import com.example.users.Source.remote.UserfirestoreClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Singleton

@InstallIn(Singleton::class)
@Module
object RemoteSourceModule {
    @Provides
    fun provideUserRemoteDataSource(firestore: FirebaseFirestore): UserRemoteDataSource = UserfirestoreClient(firestore)
    @Provides
    fun provideFirestoreObject():FirebaseFirestore = Firebase.firestore

}