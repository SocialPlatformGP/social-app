package com.gp.core.di

import com.gp.core.source.local.ReplyLocalDataSource
import com.gp.core.source.local.ReplyLocalDataSourceImpl
import com.gp.core.database.dao.ReplyDao
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