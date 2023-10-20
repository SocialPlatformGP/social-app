package com.gp.users.di

import com.gp.users.Source.local.UserLocalDataSourceImp
import com.gp.users.Source.local.UserLocalDataSource
import com.gp.socialapp.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object LocalSourceModule {
    @Provides
    fun provideUserLocalDataSource(userDao: UserDao): UserLocalDataSource = UserLocalDataSourceImp(userDao)
}