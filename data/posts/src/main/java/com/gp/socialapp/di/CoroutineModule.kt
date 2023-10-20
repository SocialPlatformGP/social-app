package com.gp.socialapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    fun provideIoDispatcher() :CoroutineScope{
        return CoroutineScope(Dispatchers.IO)
    }
}