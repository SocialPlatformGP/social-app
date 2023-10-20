package com.gp.socialapp.di

import android.content.Context
import com.gp.socialapp.utils.NetworkStatus
import com.gp.socialapp.utils.NetworkStatusImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkStatusModule {
    @Provides
    fun provideNetworkStatus(@ApplicationContext context: Context): NetworkStatus {
        return NetworkStatusImpl(context)
    }
}