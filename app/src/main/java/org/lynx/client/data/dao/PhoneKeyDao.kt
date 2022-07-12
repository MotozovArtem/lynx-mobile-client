package org.lynx.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.lynx.client.data.model.AbonentKey
import org.lynx.client.data.model.PhoneKey

@Dao
interface PhoneKeyDao {

    @Query("SELECT * FROM PhoneKey WHERE keyId = 1 LIMIT 1")
    suspend fun findKey(): PhoneKey?

    @Insert
    suspend fun insert(phoneKey: PhoneKey)

    @Query("UPDATE PhoneKey SET privateKey = :privateKey, publicKey = :publicKey, lastModifiedDate = DATETIME('now') WHERE keyId = 1")
    suspend fun updateKey(privateKey: String, publicKey: String)

    @Delete
    fun delete(phoneKey: PhoneKey): Int
}

@Dao
interface AbonentKeyDao {

    @Query("SELECT * FROM AbonentKey")
    fun findAll(): Flow<List<AbonentKey>>

    @Query("SELECT * FROM AbonentKey WHERE abonentName = :abonent")
    fun findAbonentKey(abonent: String): AbonentKey

    @Query("SELECT * FROM AbonentKey WHERE abonentPublicKey = :abonentPublicKey")
    fun findAbonentKeyByPublicKey(abonentPublicKey: String): AbonentKey

    @Insert
    suspend fun insert(key: AbonentKey)

    @Query("UPDATE AbonentKey SET sharedKey = :sharedKey, abonentPublicKey = :abonentPublicKey, lastModifiedDate = DATETIME('now') WHERE abonentName = :abonentName")
    suspend fun updateAbonentKey(sharedKey: String, abonentPublicKey: String, abonentName: String)

    @Query("DELETE FROM AbonentKey WHERE abonentName = :abonentName")
    fun deleteAdonentKeyByName(abonentName: String): Int

    @Delete
    fun deleteAbonentKey(key: AbonentKey)


}