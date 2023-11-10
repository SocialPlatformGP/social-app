package com.gp.users.di

import com.gp.users.Source.local.UserLocalDataSource
import com.gp.users.Source.remote.UserRemoteDataSource
import com.gp.users.repository.UserRepository
import com.gp.users.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideUserRepository(userLocalDataSource: UserLocalDataSource, userRemoteDataSource: UserRemoteDataSource)
            : UserRepository = UserRepositoryImpl(userLocalDataSource, userRemoteDataSource)
}