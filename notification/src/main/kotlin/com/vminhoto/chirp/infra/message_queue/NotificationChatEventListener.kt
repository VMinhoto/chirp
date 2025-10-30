package com.vminhoto.chirp.infra.message_queue

import com.vminhoto.chirp.domain.events.chat.ChatEvent
import com.vminhoto.chirp.domain.events.user.UserEvent
import com.vminhoto.chirp.infra.service.EmailService
import com.vminhoto.chirp.infra.service.PushNotificationService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

/**
 * Class that Listens to Chat Events and handles notifications accordingly
 */
@Component
class NotificationChatEventListener(
    private val pushNotificationService: PushNotificationService
) {

    /**
     * This function receives a UserEvent from the NOTIFICATION_CHAT_EVENTS queue and runs the corresponding function
     * according to each type of chat event.
     * @param event of type ChatEvent
     */
    @RabbitListener(queues = [MessageQueues.NOTIFICATION_CHAT_EVENTS])
    @Transactional
    fun handleUserEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.NewMessage -> {
                pushNotificationService.sendNewMessageNotification(
                    recipientUserIds = event.recipientIds.toList(),
                    senderUserId = event.senderId,
                    senderUsername = event.senderUsername,
                    message = event.message,
                    chatId = event.chatId
                )
            }
        }
    }
}