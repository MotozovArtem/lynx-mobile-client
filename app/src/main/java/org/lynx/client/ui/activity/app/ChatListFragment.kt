package org.lynx.client.ui.activity.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.lynx.client.LynxApplication
import org.lynx.client.data.model.Message
import org.lynx.client.databinding.FragmentChatListBinding
import org.lynx.client.ui.activity.chat.ChatActivity
import org.lynx.client.ui.util.IntentKeys
import org.lynx.client.ui.viewmodel.ChatListViewModel
import org.lynx.client.ui.viewmodel.ChatListViewModelFactory

class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val chatListViewModel: ChatListViewModel by viewModels {
        ChatListViewModelFactory(
            (requireActivity().application as LynxApplication).appContainer.messageRepository,
            (requireActivity().application as LynxApplication).appContainer.cryptographyService
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        binding.chatRecyclerView.adapter = ChatListAdapter { message -> onChatClick(message) }
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.chatRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        chatListViewModel.chatList.observe(viewLifecycleOwner) { users ->
            users.let {
                (binding.chatRecyclerView.adapter as ChatListAdapter).submitList(it)
            }
        }
        return binding.root
    }

    private fun onChatClick(message: Message) {
        val intent = Intent(context, ChatActivity()::class.java)
        intent.putExtra(IntentKeys.CHAT_ID, message.chat)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}