package org.lynx.client.ui.viewmodel

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.lynx.client.LynxApplication
import org.lynx.client.data.dto.Certificate
import org.lynx.client.data.model.Message
import org.lynx.client.data.model.MessageType
import org.lynx.client.data.repository.MessageRepository
import org.lynx.client.data.repository.UserRepository
import org.lynx.client.http.LynxChatHttpClient
import org.lynx.client.service.CryptographyService
import org.lynx.client.service.MessageService
import org.lynx.client.service.UserService
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.Date

class ChatViewModel(
    private val messageRepository: MessageRepository,
    private val messageService: MessageService,
    private val userRepository: UserRepository,
    private val cryptographyService: CryptographyService,
    private val userService: UserService,
) : ViewModel() {

    private val TAG = "ChatViewModel"

    lateinit var chatId: String

    lateinit var lynxChatHttpClient: LynxChatHttpClient

    val status = MutableLiveData<Boolean?>(null)

    fun messagesFrom(sender: String): LiveData<List<Message>> =
        messageRepository.messagesFromSender(sender).map { messages ->
            messages.map { message ->
                Message(
                    message.messageId,
                    cryptographyService.decrypt(
                        message.text!!,
                        cryptographyService.getSharedKeyForAbonent(chatId)!!,
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

    fun messagesInChat(chat: String): LiveData<List<Message>> =
        messageRepository.messagesFromChat(chat).map { messages ->
            messages.map { message ->
                Message(
                    message.messageId,
                    cryptographyService.decrypt(
                        message.text!!,
                        cryptographyService.getSharedKeyForAbonent(chatId)!!,
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


    fun initChatClient(abonent: String, application: LynxApplication) {
        application.appContainer.applicationScope.launch {
            val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("localhost", 9050))
            val container = application.appContainer
            val user = userRepository.findUserByName(abonent)
            val userDomains =
                Gson().fromJson<List<String>?>(
                    user.domains,
                    object : TypeToken<List<String>>() {}.type
                )
            for (domain in userDomains) {
                container.httpClient = container.httpClientBuilder.proxy(proxy).build()
                container.retrofitBuilder.baseUrl("http://${domain}")
                    .client(container.httpClient!!)
                container.retrofit = container.retrofitBuilder.build()
                container.isRetrofitClientInitialized = true
                // FIXME: Create http client for every domain
                container.lynxChatHttpClient = container.retrofit?.create(
                    LynxChatHttpClient::class.java
                )!!
                lynxChatHttpClient = container.lynxChatHttpClient!!
            }
        }

    }

    fun sendMessage(message: String) = viewModelScope.launch {
        var abonentSharedKey = cryptographyService.getSharedKeyForAbonent(chatId)
        if (abonentSharedKey == null) {
            val sendCert = lynxChatHttpClient.sendCert(
                Certificate(
                    userService.authenticatedUser,
                    cryptographyService.getX509PublicKeyAsBase64()
                )
            )
            when (sendCert.body()) {
                is Certificate -> {
                    status.value = true
                    Log.i(TAG, "Abonent public key received")
                    val abonentCertificate = sendCert.body()!!
                    abonentSharedKey = cryptographyService.generateSharedKeyForAbonent(
                        abonentCertificate.username,
                        abonentCertificate.publicKey
                    )
                }
                else -> {
                    Log.e(TAG, "Key exchange failed")
                    status.value = false
                    return@launch
                }
            }
        }
        val sender = userService.authenticatedUser
        val iv = cryptographyService.generateIv()
        val encryptedMessage = cryptographyService.encrypt(
            message,
            abonentSharedKey,
            iv
        )
        val newMessage = Message(
            0,
            Base64.encodeToString(encryptedMessage, Base64.NO_WRAP),
            sender,
            chatId,
            Base64.encodeToString(iv, Base64.NO_WRAP),
            MessageType.MESSAGE,
            null,
            null,
            false,
            Date(),
            userService.authenticatedUser
        )
        messageService.sendMessage(newMessage)
    }
}

class ChatViewModelFactory(
    private val repository: MessageRepository,
    private val messageService: MessageService,
    private val userRepository: UserRepository,
    private val cryptographyService: CryptographyService,
    private val userService: UserService,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(
                repository,
                messageService,
                userRepository,
                cryptographyService,
                userService,
            ) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }

}