package com.example.posts.di

import com.example.posts.source.local.PostLocalDataSource
import com.example.posts.source.local.PostLocalDataSourceImpl
import com.gp.socialapp.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocalSourceModule {
@Provides
fun providePostLocalDataSource(postDao: PostDao): PostLocalDataSource = PostLocalDataSourceImpl(postDao)
}