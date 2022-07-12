package org.lynx.client.ui.list

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
import org.lynx.client.ui.util.Formatters


class ChatAdapter(private val authenticatedUser: String) :
    ListAdapter<Message, RecyclerView.ViewHolder>(ChatComparator()) {
    private val VIEW_TYPE_MESSAGE_SENT: Int = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED: Int = 2
    private var messages: List<Message>? = null
    // FIXME: Keep in mind about very old messages (older than year)

    fun updateMessages(messages: List<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = messages?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (messages == null) {
            return
        }
        val message: Message = messages!![position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = messages!![position]
        if (message.sender == authenticatedUser) {
            return VIEW_TYPE_MESSAGE_SENT
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return SentMessageHolder.create(parent)
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            return ReceivedMessageHolder.create(parent)
        }
        throw IllegalArgumentException("Illegal ViewHolder type received")
    }

    class ChatComparator : DiffUtil.ItemCallback<Message>() {
        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.text === newItem.text && oldItem.creation_date == newItem.creation_date
        }

        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem === newItem
        }
    }
}

class ReceivedMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var textChatOtherName: TextView = itemView.findViewById(R.id.text_chat_other_name)
    private var textChatMessageOther: TextView = itemView.findViewById(R.id.text_chat_message_other)
    private var textChatMessageTimeOther: TextView =
        itemView.findViewById(R.id.text_chat_message_time_other)

    fun bind(message: Message) {
        textChatOtherName.text = message.chat
        textChatMessageOther.text = message.text
        textChatMessageTimeOther.text =
            Formatters.dateTimeFormatter.format(message.creation_date)
    }

    companion object {
        fun create(parent: ViewGroup): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_message_other, parent, false)
            return ReceivedMessageHolder(view)
        }
    }
}

class SentMessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var textChatMessageOwner: TextView = itemView.findViewById(R.id.text_chat_message_owner)
    private var textChatMessageTimeOwner: TextView =
        itemView.findViewById(R.id.text_chat_message_time_owner)

    fun bind(message: Message) {
        textChatMessageOwner.text = message.text
        textChatMessageTimeOwner.text =
            Formatters.dateTimeFormatter.format(message.creation_date)
    }


    companion object {
        fun create(parent: ViewGroup): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_message_owner, parent, false)
            return SentMessageHolder(view)
        }
    }

}

