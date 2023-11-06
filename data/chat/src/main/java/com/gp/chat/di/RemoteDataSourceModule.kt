package com.gp.chat.di

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
    fun provideMessageRemoteDataSource(): MessageRemoteDataSource {
        return MessageFirebaseClient()
    }
}