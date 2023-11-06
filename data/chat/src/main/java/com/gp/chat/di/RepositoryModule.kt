package com.gp.chat.di

import com.gp.chat.repository.MessageRepository
import com.gp.chat.repository.MessageRepositoryImpl
import com.gp.chat.source.remote.MessageFirebaseClient
import com.gp.chat.source.remote.MessageRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideMessageRepository(remoteDataSource: MessageRemoteDataSource): MessageRepository {
        return MessageRepositoryImpl(remoteDataSource)
    }
}