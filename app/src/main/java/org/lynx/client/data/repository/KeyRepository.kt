package org.lynx.client.data.repository

import androidx.annotation.WorkerThread
import org.lynx.client.data.model.PhoneKey
import org.lynx.client.data.dao.PhoneKeyDao

class KeyRepository(private val phoneKeyDao: PhoneKeyDao) {

    suspend fun getPhoneKey(): PhoneKey? = phoneKeyDao.findKey()

    @WorkerThread
    suspend fun insert(phoneKey: PhoneKey) {
        phoneKeyDao.insert(phoneKey)
    }

    @WorkerThread
    suspend fun update(phoneKey: PhoneKey) {
        phoneKeyDao.updateKey(phoneKey.privateKey, phoneKey.publicKey)
    }
}