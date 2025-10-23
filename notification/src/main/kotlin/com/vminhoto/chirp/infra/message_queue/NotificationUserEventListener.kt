package com.vminhoto.chirp.infra.message_queue

import com.vminhoto.chirp.domain.events.user.UserEvent
import com.vminhoto.chirp.infra.service.EmailService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

/**
 * Class that Listens to User Events and handles notifications accordingly
 */
@Component
class NotificationUserEventListener(private val emailService: EmailService) {

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

                emailService.sendVerificationEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    token = event.verificationToken
                )
            }
            is UserEvent.RequestResetPassword -> {
                emailService.sendPasswordResetEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    token = event.passwordResetToken,
                    expiresIn = Duration.ofMinutes(event.expiresInMinutes)
                )

            }
            is UserEvent.RequestResendVerification -> {
                emailService.sendVerificationEmail(
                    email = event.email,
                    username = event.username,
                    userId = event.userId,
                    token = event.verificationToken
                )

            }
            else -> Unit
        }
    }
}