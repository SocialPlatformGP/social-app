package com.gp.core.di

import com.gp.core.database.dao.ReplyDao
import com.gp.core.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db:AppDatabase): ReplyDao = db.postDao()
}