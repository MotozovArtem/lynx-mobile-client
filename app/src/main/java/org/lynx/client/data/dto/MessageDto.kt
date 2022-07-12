package org.lynx.client.data.dto

import org.lynx.client.data.model.Message
import org.lynx.client.data.model.MessageType
import java.util.Date

data class MessageDto(
    val message: String,
    val username: String, // sender
    val chat: String,
    val iv: String
)

fun MessageDto.toDomainFromReceivedMessage(owner: String): Message =
    Message(
        0,
        message,
        username,
        username,
        iv,
        MessageType.MESSAGE,
        null,
        null,
        true,
        Date(),
        owner
    )

fun MessageDto.toDomain(owner: String): Message = Message(
    0,
    message,
    username,
    chat,
    iv,
    MessageType.MESSAGE,
    null,
    null,
    true,
    Date(),
    owner
)