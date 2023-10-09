package com.gp.socialapp.di

import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.PostRepositoryImpl
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.repository.ReplyRepositoryImpl
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun provideReplyRepository(replyLocalDataSource: ReplyLocalDataSource,replyRemoteDataSource: ReplyRemoteDataSource): ReplyRepository {
        return ReplyRepositoryImpl(replyLocalDataSource,replyRemoteDataSource)
    }
    @Provides
    fun providePostRepository(postLocalDataSource: PostLocalDataSource, postRemoteDataSource: PostRemoteDataSource)
    : PostRepository = PostRepositoryImpl(postLocalDataSource, postRemoteDataSource)
}