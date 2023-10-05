package com.gp.socialapp.di.post

import com.gp.socialapp.dao.PostDao
import com.gp.socialapp.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    fun providePostDao(db:AppDatabase): PostDao = db.postDao()
}