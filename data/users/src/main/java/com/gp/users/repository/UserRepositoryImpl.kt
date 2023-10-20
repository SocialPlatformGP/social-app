package com.gp.users.repository

import com.gp.socialapp.database.model.UserEntity
import com.gp.users.Source.local.UserLocalDataSource
import com.gp.users.Source.remote.UserRemoteDataSource
import com.gp.users.model.NetworkUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userLocalSource: UserLocalDataSource,
                                             private val userRemoteSource: UserRemoteDataSource
) :UserRepository {
    override suspend fun insertLocalUser(userEntity: UserEntity) {
        userLocalSource.insertUser(userEntity)
    }

    override suspend fun updateLocalUser(userEntity: UserEntity) {
       userLocalSource.updateUser(userEntity)
    }

    override suspend fun deleteLocalUser(userEntity: UserEntity) {
        userLocalSource.deleteUser(userEntity)
    }

    override suspend fun getAllLocalUsers(): Flow<List<UserEntity>> {
        return userLocalSource.getAllUsers()
    }
    override fun createNetworkUser(user: NetworkUser) = userRemoteSource.createUser(user)

    override fun updateNetworkUser(user: UserEntity) = userRemoteSource.updateUser(user)

    override fun deleteNetworkUser(user: UserEntity) = userRemoteSource.deleteUser(user)
}