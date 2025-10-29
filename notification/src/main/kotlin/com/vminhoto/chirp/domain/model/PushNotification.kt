package com.vminhoto.chirp.domain.model

import com.vminhoto.chirp.domain.type.ChatId
import java.util.UUID

/**
 * Data class representing the domain Push Notification Model
 * @param id the Id of the push notification
 * @param title the title of the notification
 * @param recipients list of devices this notification is to be sent.
 * @param message the message of the notification
 * @param chatId what chat this notificaion belongs to.
 * @param data aditional data.
 */
data class PushNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val recipients: List<DeviceToken>,
    val message: String,
    val chatId: ChatId,
    val data: Map<String, String>
)
