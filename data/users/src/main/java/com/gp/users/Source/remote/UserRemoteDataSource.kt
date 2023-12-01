package com.gp.users.Source.remote

import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {
    fun createUser(user: NetworkUser): Flow<State<Nothing>>
    fun updateUser(user: UserEntity): Flow<State<Nothing>>
    fun deleteUser(user: UserEntity): Flow<State<Nothing>>
    suspend fun  fetchUser(email: String): State<NetworkUser>
    fun fetchUsers(): Flow<State<List<User>>>
    fun getCurrentUserEmail(): String
    fun getUsersByEmails(emails: List<String>): Flow<State<List<User>>>
}