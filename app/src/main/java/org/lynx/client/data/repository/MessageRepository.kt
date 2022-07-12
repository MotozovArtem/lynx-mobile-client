package org.lynx.client.data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import org.lynx.client.data.dao.MessageDao
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.MessageType
import org.lynx.client.service.UserService

class MessageRepository(private val messageDao: MessageDao, private val userService: UserService) {
    val chatList: Flow<List<Message>> = messageDao.findAllUniqueChats(userService.authenticatedUser)

    val unreadChat: Flow<List<Message>> =
        messageDao.findAllUniqueUnreadChat(userService.authenticatedUser)

    fun messagesFromSender(sender: String): Flow<List<Message>> =
        messageDao.findAllMessagesFromSender(sender, userService.authenticatedUser)

    fun messagesFromChat(chat: String): Flow<List<Message>> =
        messageDao.findAllMessagesInChat(chat, userService.authenticatedUser)

    fun messagesFromChatWithType(chat: String, messageType: MessageType): Flow<List<Message>> =
        messageDao.findMessagesInChatWithType(chat, messageType, userService.authenticatedUser)

    @WorkerThread
    suspend fun insert(message: Message) {
        messageDao.insert(message)
    }
}