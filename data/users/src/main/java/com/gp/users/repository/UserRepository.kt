package com.gp.users.repository

import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insertLocalUser(userEntity: UserEntity)

    suspend fun updateLocalUser(userEntity: UserEntity)

    suspend fun deleteLocalUser(userEntity: UserEntity)

    suspend fun getAllLocalUsers(): Flow<List<UserEntity>>

    fun createNetworkUser(user: NetworkUser): Flow<State<Nothing>>
    fun updateNetworkUser(user: UserEntity): Flow<State<Nothing>>
    fun deleteNetworkUser(user: UserEntity): Flow<State<Nothing>>
    suspend fun  fetchUser(email: String): State<NetworkUser>
    suspend fun getUserById(email:String): UserEntity
    suspend fun fetchAllUsers():Flow<List<UserEntity>>


}