package org.lynx.client.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.MessageType
import org.lynx.client.data.repository.MessageRepository
import org.lynx.client.service.CryptographyService

class ChatFilesViewModel(
    private val messageRepository: MessageRepository,
    private val cryptographyService: CryptographyService
) : ViewModel() {

    fun getFilesList(chat: String): LiveData<List<Message>> =
        messageRepository.messagesFromChatWithType(chat, MessageType.FILE).asLiveData()

}

class ChatFilesViewModelFactory(
    private val messageRepository: MessageRepository,
    private val cryptographyService: CryptographyService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatFilesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatFilesViewModel(messageRepository, cryptographyService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}