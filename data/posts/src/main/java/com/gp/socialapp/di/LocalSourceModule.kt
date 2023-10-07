package com.gp.socialapp.di

import com.gp.socialapp.database.dao.PostDao
import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.local.ReplyLocalDataSourceImpl
import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.source.local.PostLocalDataSource
import com.gp.socialapp.source.local.PostLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocalSourceModule {
    @Provides
    fun provideReplyLocalDataSource(replyDao: ReplyDao): ReplyLocalDataSource = ReplyLocalDataSourceImpl(replyDao)
    @Provides
    fun providePostLocalDataSource(postDao: PostDao): PostLocalDataSource = PostLocalDataSourceImpl(postDao)
}