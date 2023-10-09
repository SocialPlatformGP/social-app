package com.example.users.di

import com.example.users.Source.local.UserLocalDataSource
import com.example.users.Source.remote.UserRemoteDataSource
import com.example.users.repository.UserRepository
import com.example.users.repository.UserRepositoryImp
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides

@InstallIn(SingletonComponent::class)

@Module
object RepositoryModule {
    @Provides
    fun provideUserRepository(userLocalDataSource: UserLocalDataSource, userRemoteDataSource: UserRemoteDataSource)
            : UserRepository = UserRepositoryImp(userLocalDataSource, userRemoteDataSource)
}