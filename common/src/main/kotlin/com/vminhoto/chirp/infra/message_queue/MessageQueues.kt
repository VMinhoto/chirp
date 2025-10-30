package com.vminhoto.chirp.infra.message_queue

/**
 * Object that holds all keys representing the queues events are sent to
 */
object MessageQueues {
    const val NOTIFICATION_USER_EVENTS = "notification_user_events"
    const val NOTIFICATION_CHAT_EVENTS = "notification_chat_events"
    const val CHAT_USER_EVENTS = "chat_user_events"
}