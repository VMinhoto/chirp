package com.vminhoto.chirp.api.dto.ws

/**
 * Possible type (client -> server) for Incoming Web Sockets Message
 */
enum class IncomingWebSocketMessageType {
    NEW_MESSAGE
}

/**
 * Possible type (server -> client) for Outgoing Web Sockets Message
 */
enum class OutgoingWebSocketMessageType {
    NEW_MESSAGE,
    MESSAGE_DELETED,
    PROFILE_PICTURE_UPDATED,
    CHAT_PARTICIPANTS_CHANGED,
    ERROR
}

/**
 * Data class representing an Incoming Web Socket Message. Has the type of the message so the correct serializer -
 * deserializer can be used for the payload according to te type.
 * @param type Type of incoming message.
 * @param payload payload of the message.
 */
data class IncomingWebSocketMessage(
    val type: IncomingWebSocketMessageType,
    val payload: String
)

/**
 * Data class representing an Outgoing Web Socket Message. Has the type of the message so the correct serializer -
 * deserializer can be used for the payload according to te type.
 * @param type Type of incoming message.
 * @param payload payload of the message.
 */
data class OutgoingWebSocketMessage(
    val type: OutgoingWebSocketMessageType,
    val payload: String
)