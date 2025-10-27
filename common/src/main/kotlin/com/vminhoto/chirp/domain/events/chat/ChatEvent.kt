package com.vminhoto.chirp.domain.events.chat

import com.vminhoto.chirp.domain.events.ChirpEvent
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import java.time.Instant
import java.util.UUID

/**
 * Sealed class representing the possible ChatEvents (on RabbitMQ) that can exist. Extends ChirpEvent
 * @property eventId represents the ID of the event.
 * @property occurredAt Instant object representing the time of which the event was fired. Default value is USER_EXCHANGE
 * @property exchange Representation of the queue that reroutes the event to the consumer. Default value is now()
 */
sealed class ChatEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = ChatEventConstants.CHAT_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
): ChirpEvent {

    /**
     * Data Class representing the message creation event. Implements ChirpEvent
     * @property senderId UUID of the User that sent the message.
     * @property senderUsername email of the User that sent the message.
     * @property recipientIds Id of the users subscribed to that chat.
     * @property chatId If of the Chat the message is to be sent.
     * @property message The Message.
     * @property eventKey key related to the event type. Default: USER_CREATED_KEY
     *
     */
    data class NewMessage(
        val senderId: UserId,
        val senderUsername: String,
        val recipientIds: Set<UserId>,
        val chatId: ChatId,
        val message: String,
        override val eventKey: String = ChatEventConstants.CHAT_NEW_MESSAGE
    ): ChatEvent(), ChirpEvent
}