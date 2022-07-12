package org.lynx.client.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.lynx.client.data.dto.MessageDto
import java.util.Date

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "domain") val domains: String, // Save in JSON List
    @ColumnInfo(name = "online") val online: Boolean,
    @ColumnInfo(name = "has_new_message") val hasNewMessage: Boolean
)

enum class MessageType {
    MESSAGE,
    FILE,
    IMAGE
}

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true) val messageId: Long,
    @ColumnInfo(name = "text") val text: String?,
    @ColumnInfo(name = "sender") val sender: String,
    @ColumnInfo(name = "chat") val chat: String,
    @ColumnInfo(name = "iv") val iv: String,
    @ColumnInfo(name = "message_type") val messageType: MessageType,
    @ColumnInfo(name = "file_name") val fileName: String?,
    @ColumnInfo(name = "file_data") val fileData: String?,
    @ColumnInfo(name = "unread") val unread: Boolean,
    @ColumnInfo(name = "creationDate") val creation_date: Date,
    @ColumnInfo(name = "owner") val owner: String
)

fun Message.toDto(): MessageDto = MessageDto(text!!, sender, chat, iv)