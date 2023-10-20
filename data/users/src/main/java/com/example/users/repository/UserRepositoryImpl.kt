package com.example.users.repository

import com.example.users.Source.local.UserLocalDataSource
import com.example.users.Source.remote.UserRemoteDataSource
import com.example.users.model.NetworkUser
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userLocalSource: UserLocalDataSource,
                                             private val userRemoteSource: UserRemoteDataSource) :UserRepository {
    override suspend fun insertUser(userEntity: UserEntity) {
        userLocalSource.insertUser(userEntity)
    }

    override suspend fun updateUser(userEntity: UserEntity) {
       userLocalSource.updateUser(userEntity)
    }

    override suspend fun deleteUser(userEntity: UserEntity) {
        userLocalSource.deleteUser(userEntity)
    }

    override suspend fun getAllUsers(): Flow<List<UserEntity>> {
        return userLocalSource.getAllUsers()
    }

    override suspend fun getUserByID(userID: String) {
        userLocalSource.getUserByID(userID)
    }

    override suspend fun createPost(user: NetworkUser) {
        userRemoteSource.createPost(user)
    }

    override suspend fun updatePost(user: UserEntity) {
      userRemoteSource.updatePost(user)
    }

    override suspend fun fetchPosts(): Flow<List<NetworkUser>> {
        return userRemoteSource.fetchPosts()
    }

    override suspend fun deletePost(user: UserEntity) {
        userRemoteSource.deletePost(user)
    }
}