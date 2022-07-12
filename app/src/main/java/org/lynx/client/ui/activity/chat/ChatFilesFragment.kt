package org.lynx.client.ui.activity.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import org.lynx.client.LynxApplication
import org.lynx.client.R
import org.lynx.client.databinding.FragmentChatFilesBinding
import org.lynx.client.ui.viewmodel.ChatFilesViewModel
import org.lynx.client.ui.viewmodel.ChatFilesViewModelFactory

class ChatFilesFragment : Fragment() {
    private var _binding: FragmentChatFilesBinding? = null
    private val binding get() = _binding!!
    private val chatFilesViewModel: ChatFilesViewModel by viewModels {
        val container = (requireActivity().application as LynxApplication).appContainer
        ChatFilesViewModelFactory(container.messageRepository, container.cryptographyService)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatFilesBinding.inflate(inflater, container, false)
        binding
        return inflater.inflate(R.layout.fragment_chat_files, container, false)
    }


}