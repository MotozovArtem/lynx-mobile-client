package org.lynx.client.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.MessageType

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE unread = 1 AND owner = :ownerName GROUP BY chat")
    fun findAllUniqueUnreadChat(ownerName: String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE owner = :ownerName GROUP BY chat")
    fun findAllUniqueChats(ownerName: String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE sender = :sender AND owner = :ownerName ORDER BY creationDate ASC")
    fun findAllMessagesFromSender(sender: String, ownerName: String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE chat = :chat AND owner = :ownerName ORDER BY creationDate ASC")
    fun findAllMessagesInChat(chat: String, ownerName: String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE chat = :chat AND chat != sender AND owner = :ownerName ORDER BY creationDate ASC")
    fun findAllReceivedMessagesInChat(chat: String, ownerName: String): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE sender = :sender AND owner = :ownerName ")
    fun findMessagesByUser(sender: String, ownerName: String): List<Message>

    @Query("SELECT * FROM Message WHERE sender = :sender AND owner = :ownerName LIMIT :limit")
    fun findLastMessagesByUser(sender: String, ownerName: String, limit: Int): List<Message>

    @Query("SELECT * FROM Message WHERE chat = :chat AND message_type = :messageType AND owner = :ownerName ORDER BY creationDate ASC")
    fun findMessagesInChatWithType(
        chat: String,
        messageType: MessageType,
        ownerName: String
    ): Flow<List<Message>>

    @Query("SELECT * FROM Message WHERE unread = 1")
    fun findAllUnreadMessages(): List<Message>

    @Query("SELECT * FROM Message WHERE unread = 1 AND sender = :sender")
    fun findAllUnreadMessagesFromUser(sender: String): List<Message>

    @Query("UPDATE Message SET unread = 0 WHERE messageId = :messageId")
    fun setUnreadToFalseForMessage(messageId: Long)

    @Insert
    suspend fun insert(message: Message)

    @Delete
    fun delete(message: Message): Int

    @Query("DELETE FROM Message WHERE owner = :ownerName")
    fun deleteAllForUser(ownerName: String)

    @Query("DELETE FROM Message")
    fun deleteAll()

    @Query("DELETE FROM Message WHERE sender = :sender AND owner = :ownerName")
    fun deleteBySender(sender: String, ownerName: String): Int
}