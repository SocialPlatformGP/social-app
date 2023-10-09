package com.example.users.repository

import com.example.users.Source.local.UserLocalDataSource
import com.example.users.Source.remote.UserRemoteDataSource
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImp @Inject constructor( private val userLocalSource: UserLocalDataSource,
                                             private val userRemoteSource: UserRemoteDataSource) :UserRepository {
    override suspend fun insertUser(userEntity: UserEntity) {
        userLocalSource.insertUser(userEntity)
    }

    override suspend fun updateUser(userEntity: UserEntity) {
       userLocalSource.updateUser(userEntity)
    }

    override suspend fun deleteUser(userEntity: UserEntity) {
        userLocalSource.DeleteUser(userEntity)
    }

    override suspend fun getAllUsers(): Flow<List<UserEntity>> {
        return userLocalSource.getAllUsers()
    }

    override suspend fun getUserByID(userID: String) {
        userLocalSource.getUserByID(userID)
    }
}