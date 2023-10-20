package com.gp.users.Source.local
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {
    suspend fun insertUser(user:UserEntity)
    suspend fun updateUser(user:UserEntity)
    suspend fun getAllUsers(): Flow<List<UserEntity>>
    suspend fun deleteUser(user:UserEntity)
}