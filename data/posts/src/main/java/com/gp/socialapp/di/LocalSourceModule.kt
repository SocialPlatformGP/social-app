package com.gp.socialapp.di

import com.gp.socialapp.source.local.ReplyLocalDataSource
import com.gp.socialapp.source.local.ReplyLocalDataSourceImpl
import com.gp.socialapp.database.dao.ReplyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocalSourceModule {
@Provides
fun providePostLocalDataSource(replyDao: ReplyDao): ReplyLocalDataSource = ReplyLocalDataSourceImpl(replyDao)
}