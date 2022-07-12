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
import org.lynx.client.data.model.User
import org.lynx.client.databinding.FragmentUserBinding
import org.lynx.client.ui.activity.chat.ChatActivity
import org.lynx.client.ui.util.IntentKeys
import org.lynx.client.ui.viewmodel.UserListViewModel
import org.lynx.client.ui.viewmodel.UserListViewModelFactory

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    private val userListViewModel: UserListViewModel by viewModels {
        UserListViewModelFactory((requireActivity().application as LynxApplication).appContainer.userRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        binding.userRecyclerView.adapter = UserListAdapter { user -> onUserClick(user) }
        binding.userRecyclerView.layoutManager = LinearLayoutManager(context)

        binding.userRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        userListViewModel.updateUserList()
        userListViewModel.userList.observe(viewLifecycleOwner) { users ->
            users.let {
                (binding.userRecyclerView.adapter as UserListAdapter).submitList(it)
            }
        }
        return binding.root
    }

    private fun onUserClick(user: User) {
        val intent = Intent(context, ChatActivity()::class.java)
        intent.putExtra(IntentKeys.CHAT_ID, user.username)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}