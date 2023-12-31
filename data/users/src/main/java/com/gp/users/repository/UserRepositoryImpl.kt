package com.gp.users.repository

import android.net.Uri
import android.util.Log
import com.gp.socialapp.database.model.UserEntity
import com.gp.socialapp.utils.State
import com.gp.users.Source.local.UserLocalDataSource
import com.gp.users.Source.remote.UserRemoteDataSource
import com.gp.users.model.NetworkUser
import com.gp.users.model.User
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
        deleteAllUsers()
        return userLocalSource.getAllUsers()
    }

    override fun createNetworkUser(user: NetworkUser, pfpURI: Uri) = userRemoteSource.createUser(user, pfpURI)

    override fun updateNetworkUser(user: UserEntity) = userRemoteSource.updateUser(user)

    override fun deleteNetworkUser(user: UserEntity) = userRemoteSource.deleteUser(user)

    override suspend fun fetchUser(email: String): State<NetworkUser> =
        userRemoteSource.fetchUser(email)

    override suspend fun getUserById(email: String) = userLocalSource.getUserById(email)

    override fun fetchUsers(): Flow<State<List<User>>> {
        Log.d("TAG", "fetchUsers: Repository")
        return userRemoteSource.fetchUsers()
    }

    override fun getCurrentUserEmail() = userRemoteSource.getCurrentUserEmail()
    override fun getUsersByEmails(emails: List<String>): Flow<State<List<User>>> {
        return userRemoteSource.getUsersByEmails(emails)
    }
    override fun deleteAllUsers() {
        userLocalSource.deleteAllUsers()
    }
}