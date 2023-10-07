package com.gp.socialapp.di

import com.gp.socialapp.database.dao.ReplyDao
import com.gp.socialapp.database.AppDatabase
import com.gp.socialapp.database.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun provideReplyDao(db:AppDatabase): ReplyDao = db.replyDao()
    @Provides
    fun providePostDao(db:AppDatabase): PostDao = db.postDao()
}