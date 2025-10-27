package com.vminhoto.chirp.infra.messaging

import com.vminhoto.chirp.domain.events.user.UserEvent
import com.vminhoto.chirp.domain.models.ChatParticipant
import com.vminhoto.chirp.infra.message_queue.MessageQueues
import com.vminhoto.chirp.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * Class containing the Listener for the chat user event fired when user validates the account. It creates a new Chat
 * Participant.
 * @property chatParticipantService Service responsible for the chat participant creation.
 */
@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService
) {

    /**
     * Funcion that creates the chat participant with the data from a CHAT_USER_EVENT using the event data. We only
     * listen to the UserEvent Verified.
     * @param event UserEvent that was fired
     */
    @RabbitListener(queues = [MessageQueues.CHAT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent){
        when (event) {
            is UserEvent.Verified -> {
                chatParticipantService.createChatParticipant(
                    chatParticipant = ChatParticipant(
                        userId = event.userId,
                        username = event.username,
                        email = event.email,
                        profilePictureUrl = null
                    )
                )
            }
            else -> Unit
        }
    }
}