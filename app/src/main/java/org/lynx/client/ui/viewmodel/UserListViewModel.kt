package org.lynx.client.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lynx.client.data.model.User
import org.lynx.client.data.repository.UserRepository

class UserListViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    val userList: LiveData<List<User>> = userRepository.usersList.asLiveData()

    val onlineUserList: LiveData<List<User>> = userRepository.onlineUsers.asLiveData()

    fun updateUserList() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userRepository.updateUserList()
        }
    }

}

class UserListViewModelFactory(
    private val userRepository: UserRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserListViewModel(userRepository) as T
        }
        throw IllegalAccessException("Unknown ViewModel class")
    }
}