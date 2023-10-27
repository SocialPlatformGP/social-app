package com.gp.auth.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.gp.auth.R
import com.gp.auth.network.AuthenticationFirebaseClient
import com.gp.auth.network.AuthenticationRemoteDataSource
import com.gp.auth.repo.AuthenticationRepository
import com.gp.auth.repo.AuthenticationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AuthenticationModule {
    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth
    @Provides
    fun providesAuthenticationRemoteDatasource(firebaseAuth: FirebaseAuth): AuthenticationRemoteDataSource
    = AuthenticationFirebaseClient(firebaseAuth)
    @Provides
    fun providesAuthenticationRepository(remoteDataSource: AuthenticationRemoteDataSource): AuthenticationRepository
    = AuthenticationRepositoryImpl(remoteDataSource)
}