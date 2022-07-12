package org.lynx.client.ui.activity.chat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.lynx.client.LynxApplication
import org.lynx.client.R
import org.lynx.client.databinding.ActivityChatBinding
import org.lynx.client.ui.list.ChatAdapter
import org.lynx.client.ui.util.IntentKeys
import org.lynx.client.ui.viewmodel.ChatViewModel
import org.lynx.client.ui.viewmodel.ChatViewModelFactory

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatId: String

    private val chatViewModel: ChatViewModel by viewModels {
        val container = (application as LynxApplication).appContainer

        ChatViewModelFactory(
            container.messageRepository,
            container.messageService,
            container.userRepository,
            container.cryptographyService,
            container.userService
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        setContentView(view)

        chatId = intent.getStringExtra(IntentKeys.CHAT_ID)!!
        chatViewModel.chatId = chatId

        binding.chatName.text = chatId
        binding.toolbarReturn.setOnClickListener {
            finish()
        }

        val chatAdapter =
            ChatAdapter(
                (application as LynxApplication)
                    .appContainer.userService.authenticatedUser
            )
        binding.messageListRecyclerView.adapter = chatAdapter
        binding.messageListRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        chatViewModel.initChatClient(chatId, application as LynxApplication)
        chatViewModel.status.observe(this) { status ->
            status?.let {
                chatViewModel.status.value = null
                if (!status) {
                    Toast.makeText(
                        this@ChatActivity,
                        getString(R.string.failedKeyExchangeMessageToast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        chatViewModel.messagesInChat(chatId).observe(this) { messages ->
            messages.let {
                (binding.messageListRecyclerView.adapter as ChatAdapter).updateMessages(it)
            }
        }
        binding.sendMessageButton.setOnClickListener { onSendMessage() }
    }

    private fun onSendMessage() {
        val messageText = binding.messageEditor.text.toString()
        chatViewModel.sendMessage(messageText)
        binding.messageEditor.text.clear()
    }
}