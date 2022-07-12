package org.lynx.client.data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import org.lynx.client.data.model.AbonentKey
import org.lynx.client.data.dao.AbonentKeyDao

class AbonentKeyRepository(private val abonentKeyDao: AbonentKeyDao) {
    val abonentKeys: Flow<List<AbonentKey>> = abonentKeyDao.findAll()

    fun getAbonentKey(abonentName: String) = abonentKeyDao.findAbonentKey(abonentName)

    fun getAbonentKeyByPublicKey(abonentPublicKey: String) =
        abonentKeyDao.findAbonentKeyByPublicKey(abonentPublicKey)

    @WorkerThread
    suspend fun insert(abonentKey: AbonentKey) {
        abonentKeyDao.insert(abonentKey)
    }

    @WorkerThread
    suspend fun update(sharedKey: String, abonentPublicKey: String, abonentName: String) {
        abonentKeyDao.updateAbonentKey(sharedKey, abonentPublicKey, abonentName)
    }
}