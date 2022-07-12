package org.lynx.client.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.lynx.client.data.dao.UserDao
import org.lynx.client.data.dto.UserResponse
import org.lynx.client.data.model.User
import org.lynx.client.http.PeerStockServerHttpClient

class UserRepository(
    private val peerStockServerHttpClient: PeerStockServerHttpClient,
    private val userDao: UserDao,
) {

    private val TAG = "UserRepository"

    val usersList: Flow<List<User>> = userDao.getUsers()

    val onlineUsers: Flow<List<User>> = userDao.getOnlineUsers()

    fun findUserByName(abonent: String) = userDao.getUserByName(abonent)

    @WorkerThread
    suspend fun updateUserList() {
        withContext(Dispatchers.IO) {
            val result = peerStockServerHttpClient.getUsers();
            when (result.body()) {
                is List<UserResponse> -> {
                    val response = result.body()!!
                    userDao.deleteAll()
                    userDao.insertAll(
                        response.map {
                            User(0L, it.name, Gson().toJson(it.domain), it.online)
                        }
                    )
                }
                else -> Log.e(
                    TAG,
                    "Cannot update user list. Response code: ${result.code()}",
                    RuntimeException(result.errorBody()?.toString())
                )
            }

        }
//        _usersList.clear()
//        _usersList.addAll(result.map { User(0L, it.name, it.domain, it.online) })

    }

}