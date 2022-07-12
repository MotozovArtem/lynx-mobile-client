package org.lynx.client.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.toDto
import org.lynx.client.data.repository.MessageRepository
import org.lynx.client.di.AppContainer

interface MessageService {
    fun sendMessage(message: Message)

    fun receiveMessage(message: Message)

    fun sendFile(message: Message)

    fun receiveFile(message: Message)
}

class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val applicationScope: CoroutineScope,
    private val container: AppContainer,
    private val cryptographyService: CryptographyService
) : MessageService {

    override fun sendMessage(message: Message) {
        applicationScope.launch {
            messageRepository.insert(message)
            container.lynxChatHttpClient!!.sendMessage(message.toDto())
        }
    }

    override fun receiveMessage(message: Message) {
        applicationScope.launch {
            messageRepository.insert(message)
        }
    }

    override fun sendFile(message: Message) {
        TODO("Not yet implemented")
    }

    override fun receiveFile(message: Message) {
        TODO("Not yet implemented")
    }

}