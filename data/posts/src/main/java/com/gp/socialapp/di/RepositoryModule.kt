package com.gp.socialapp.di

import com.gp.socialapp.repository.PostRepository
import com.gp.socialapp.repository.PostRepositoryImpl
import com.gp.socialapp.repository.ReplyRepository
import com.gp.socialapp.repository.ReplyRepositoryImpl
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.remote.PostRemoteDataSource
import com.gp.socialapp.source.remote.ReplyRemoteDataSource
import com.gp.socialapp.utils.NetworkStatus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun provideReplyRepository(
        replyLocalDataSource: ReplyLocalDataSource,
        replyRemoteDataSource: ReplyRemoteDataSource,
        networkStatus:NetworkStatus,
        coroutineScope: CoroutineScope
    ): ReplyRepository {
        return ReplyRepositoryImpl(replyLocalDataSource,replyRemoteDataSource,networkStatus,coroutineScope)
    }
    @Provides
    fun providePostRepository(
        postLocalDataSource: PostLocalDataSource,
        postRemoteDataSource: PostRemoteDataSource,
        networkStatus: NetworkStatus,
        coroutineScope: CoroutineScope
        )
    : PostRepository = PostRepositoryImpl(postLocalDataSource, postRemoteDataSource,networkStatus,coroutineScope)
}