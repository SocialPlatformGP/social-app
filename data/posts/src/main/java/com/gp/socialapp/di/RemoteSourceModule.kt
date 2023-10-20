package com.gp.socialapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.gp.socialapp.source.remote.PostFirestoreClient
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.source.remote.ReplyFirestoreClient
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RemoteSourceModule {
    @Provides
    fun provideFireStore(): FirebaseFirestore = Firebase.firestore
    @Provides
    fun providePostRemoteDataSource(firestore: FirebaseFirestore): PostRemoteDataSource
        = PostFirestoreClient(firestore)

    @Provides
    fun provideReplyRemoteDataSource(firestore: FirebaseFirestore): ReplyRemoteDataSource
        = ReplyFirestoreClient(firestore)
}