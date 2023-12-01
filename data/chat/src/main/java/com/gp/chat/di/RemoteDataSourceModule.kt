package com.gp.chat.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gp.chat.source.remote.MessageFirebaseClient
import com.gp.chat.source.remote.MessageRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    fun provideMessageRemoteDataSource(database:FirebaseDatabase): MessageRemoteDataSource {
        return MessageFirebaseClient(database)
    }
    @Provides
    fun provideRealTimeDatabase(): FirebaseDatabase = Firebase.database
}