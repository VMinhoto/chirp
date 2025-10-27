package com.vminhoto.chirp.domain.events.chat

/**
 * Object to define event keys on RabbitMQ for chat events.
 */
object ChatEventConstants {
    const val CHAT_EXCHANGE = "chat.events"

    const val CHAT_NEW_MESSAGE = "chat.new_message"

}