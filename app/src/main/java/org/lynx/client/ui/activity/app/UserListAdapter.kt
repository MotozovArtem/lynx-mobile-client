package org.lynx.client.ui.activity.app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lynx.client.R
import org.lynx.client.data.model.User

class UserListAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, UserListAdapter.UserViewHolder>(UserListComparator()) {

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.create(parent, onClick)
    }

    class UserViewHolder(itemView: View, val onClick: (User) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val usernameView: TextView = itemView.findViewById(R.id.userListItemUserName)
        private val onlineView: ImageView = itemView.findViewById(R.id.userListItemOnline)

        private var currentUser: User? = null

        fun bind(user: User?) {
            currentUser = user
            usernameView.text = user?.username
            if (user?.online == true) {
                onlineView.setImageResource(R.drawable.baseline_link_black_24dp)
                onlineView.setColorFilter(R.color.lightGrey)
            } else {
                onlineView.setImageResource(R.drawable.ic_disconnected)
                onlineView.setColorFilter(R.color.blue)
            }
        }

        init {
            itemView.setOnClickListener {
                currentUser?.let {
                    onClick(it)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onClick: (User) -> Unit): UserViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_user, parent, false)
                return UserViewHolder(view, onClick)
            }
        }
    }

    class UserListComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username === newItem.username
        }
    }

}