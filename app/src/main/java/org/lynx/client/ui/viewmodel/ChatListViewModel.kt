package org.lynx.client.ui.viewmodel

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import org.lynx.client.data.model.Message
import org.lynx.client.data.repository.MessageRepository
import org.lynx.client.service.CryptographyService

class ChatListViewModel(
    private val messageRepository: MessageRepository,
    private val cryptographyService: CryptographyService
) : ViewModel() {

    val chatList: LiveData<List<Message>> = messageRepository.chatList.map { messages ->
        messages.map { message ->
            Message(
                message.messageId,
                cryptographyService.decrypt(
                    message.text!!,
                    cryptographyService.getSharedKeyForAbonent(message.chat)!!,
                    Base64.decode(message.iv, Base64.NO_WRAP)
                ),
                message.sender,
                message.chat,
                message.iv,
                message.messageType,
                message.fileName,
                message.fileData,
                message.unread,
                message.creation_date,
                message.owner
            )
        }
    }.asLiveData()

//    val allOnlineUsers: LiveData<List<Message>> = messageRepository.onlineUsers.asLiveData()
}

class ChatListViewModelFactory(
    private val messageRepository: MessageRepository,
    private val cryptographyService: CryptographyService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatListViewModel(messageRepository, cryptographyService) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }

}