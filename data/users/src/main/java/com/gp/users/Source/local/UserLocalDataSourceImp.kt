package com.gp.users.Source.local

import com.gp.socialapp.database.dao.UserDao
import com.gp.socialapp.database.model.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class UserLocalDataSourceImp @Inject constructor(private val userDao: UserDao) :
    UserLocalDataSource {
    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)

    }

    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    override suspend fun getAllUsers(): Flow<List<UserEntity>> {
       return userDao.getAllUsers()
    }

    override suspend fun deleteUser(user: UserEntity) {
       userDao.deleteUser(user)
    }

    override suspend fun getUserById(email: String)= userDao.getUserById(email)
}