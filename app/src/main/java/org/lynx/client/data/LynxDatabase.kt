package org.lynx.client.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import org.lynx.client.data.dao.AbonentKeyDao
import org.lynx.client.data.dao.MessageDao
import org.lynx.client.data.dao.PhoneKeyDao
import org.lynx.client.data.dao.UserDao
import org.lynx.client.data.model.AbonentKey
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.PhoneKey
import org.lynx.client.data.model.User

@Database(
    entities = [Message::class, PhoneKey::class, AbonentKey::class, User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LynxDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    abstract fun keyDao(): PhoneKeyDao

    abstract fun abonentKeyDao(): AbonentKeyDao


    abstract fun userDao(): UserDao

    private class PeerStockDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback()

    companion object {
        @Volatile
        private var INSTANCE: LynxDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LynxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LynxDatabase::class.java,
                    "lynx_database"
                )
                    .addCallback(PeerStockDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}