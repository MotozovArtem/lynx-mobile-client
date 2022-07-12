package org.lynx.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.lynx.client.data.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE id = :userId")
    fun getUserById(userId: String): User

    @Query("SELECT * FROM User")
    fun getUsers(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE online = 1")
    fun getOnlineUsers(): Flow<List<User>>

    @Query("SELECT * FROM User WHERE username = :username")
    fun getUserByName(username: String): User

    @Insert
    suspend fun insert(user: User)

    @Insert
    suspend fun insertAll(user: List<User>)

    @Delete
    fun delete(user: User): Int

    @Query("DELETE FROM User")
    fun deleteAll()
}