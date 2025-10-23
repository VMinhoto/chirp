package com.vminhoto.chirp.infra.message_queue

import com.vminhoto.chirp.domain.events.user.UserEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Class that Listens to User Events and handles notifications accordingly
 */
@Component
class NotificationUserEventListener {

    /**
     * This function receives a UserEvent from the NOTIFICATION_USER_EVENTS queue and runs the corresponding function
     * according to each type of user event.
     * @param event of type UserEvent
     */
    @RabbitListener(queues = [MessageQueues.NOTIFICATION_USER_EVENTS])
    @Transactional
    fun handleUserEvent(event: UserEvent) {
        when (event) {
            is UserEvent.Created -> {
                println("User created!")
            }
            is UserEvent.RequestResendPassword -> {
                println("User request resend password!")
            }
            is UserEvent.RequestResendVerification -> {
                println("User request resend verification!")
            }
            else -> Unit
        }
    }
}