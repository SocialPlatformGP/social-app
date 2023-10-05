package com.example.posts.di

import com.example.posts.repository.PostRepository
import com.example.posts.repository.PostRepositoryImpl
import com.example.posts.source.local.PostLocalDataSource
import com.gp.socialapp.dao.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun providePostRepository(postLocalDataSource: PostLocalDataSource): PostRepository {
        return PostRepositoryImpl(postLocalDataSource)
    }

}