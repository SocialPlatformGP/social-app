package com.gp.socialapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserDao {
        @Insert
        suspend fun insertUser(userEntity: UserEntity)

        @Update
        suspend fun updateUser(userEntity: UserEntity)

        @Delete
        suspend fun deleteUser(userEntity: UserEntity)

        @Query("SELECT * FROM users")
         fun getAllUsers(): Flow<List<UserEntity>>

        @Query("SELECT * FROM users where userEmail = :email")
        suspend fun getUserById(email:String): UserEntity
}