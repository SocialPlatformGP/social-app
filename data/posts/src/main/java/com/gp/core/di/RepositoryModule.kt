package com.gp.core.di

import com.gp.core.repository.ReplyRepository
import com.gp.core.repository.ReplyRepositoryImpl
import com.gp.core.source.local.ReplyLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun providePostRepository(replyLocalDataSource: ReplyLocalDataSource): ReplyRepository {
        return ReplyRepositoryImpl(replyLocalDataSource)
    }

}