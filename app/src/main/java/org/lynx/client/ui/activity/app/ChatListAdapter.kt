package org.lynx.client.ui.activity.app

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lynx.client.R
import org.lynx.client.data.model.Message

class ChatListAdapter(private val onClick: (Message) -> Unit) :
    ListAdapter<Message, ChatListAdapter.ChatViewHolder>(ChatListComparator()) {

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder.create(parent, onClick)
    }

    class ChatViewHolder(itemView: View, val onClick: (Message) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val userItemView: TextView = itemView.findViewById(R.id.chatListUserName)
        private val messageItemView: TextView = itemView.findViewById(R.id.chatListMessageText)
        private var currentMessage: Message? = null

        fun bind(message: Message?) {
            currentMessage = message
            userItemView.text = message?.chat
            messageItemView.text = message?.text
        }

        init {
            itemView.setOnClickListener {
                currentMessage?.let {
                    onClick(it)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, onClick: (Message) -> Unit): ChatViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_chat, parent, false)
                return ChatViewHolder(view, onClick)
            }
        }
    }

    class ChatListComparator : DiffUtil.ItemCallback<Message>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.chat === newItem.chat
        }

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }
    }
}