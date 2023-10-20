package com.gp.users.Source.remote

import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun createUser(user: NetworkUser): Flow<State<Nothing>>
    fun updateUser(user: UserEntity): Flow<State<Nothing>>
    fun deleteUser(user: UserEntity): Flow<State<Nothing>>
}