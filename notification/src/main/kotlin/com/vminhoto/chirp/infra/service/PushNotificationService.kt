package com.vminhoto.chirp.infra.service

import com.vminhoto.chirp.domain.exception.InvalidDeviceTokenException
import com.vminhoto.chirp.domain.model.DeviceToken
import com.vminhoto.chirp.domain.model.PushNotification
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.DeviceTokenEntity
import com.vminhoto.chirp.infra.database.DeviceTokenRepository
import com.vminhoto.chirp.infra.mappers.toDeviceToken
import com.vminhoto.chirp.infra.mappers.toPlatformEntity
import com.vminhoto.chirp.infra.push_notification.FirebasePushNotificationService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service to handle push notification business logic and operations.
 * @param deviceTokenRepository the device token repository.
 * @param firebasePushNotificationService the firebase notification service.
 */
@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Function to register a device.
     * @param userId the id of the user
     * @param token the device token as a string.
     * @param platform (IOS or ANDROID)
     * @return The resulting [DeviceToken]
     * @throws InvalidDeviceTokenException if the token is invalid.
     *
     */
    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: DeviceToken.Platform
    ): DeviceToken {
        val existing = deviceTokenRepository.findByToken(token)

        val trimmedToken = token.trim()
        if(existing == null && !firebasePushNotificationService.isValidToken(trimmedToken)) {
            throw InvalidDeviceTokenException()
        }

        val entity = if(existing!=null) {
            deviceTokenRepository.save(
                existing.apply {
                    this.userId = userId
                }
            )
        } else {
            deviceTokenRepository.save(
                DeviceTokenEntity(
                    userId = userId,
                    token = trimmedToken,
                    platform = platform.toPlatformEntity()
                    )
            )
        }

        return entity.toDeviceToken()
    }

    /**
     * Function to unregister a device by token.
     * @param token the token of the device that is to be unregistered.
     */
    @Transactional
    fun unregisterDevice(token: String){
        deviceTokenRepository.deleteByToken(token.trim())
    }

    /**
     * Function to send new message notifications.
     * @param recipientUserIds the Ids of the users this message is to be sent.
     * @param senderUserId the Id of the sender.
     * @param senderUsername the username of the sender.
     * @param message the message to be sent.
     * @param chatId the Id of the chat that fired the notification.
     */
    @Transactional
    fun sendNewMessageNotification(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        chatId: ChatId
    ){
        val deviceTokens = deviceTokenRepository.findByUserIdIn(recipientUserIds)
        if(deviceTokens.isEmpty()){
            logger.info("No device tokens found for $recipientUserIds")
            return
        }

        val recipients = deviceTokens
            .filter { it.userId != senderUserId }
            .map { it.toDeviceToken() }

        val notification = PushNotification(
            title = "New message from $senderUsername",
            recipients = recipients,
            message = message,
            chatId = chatId,
            data = mapOf(
                "chatId" to chatId.toString(),
                "type" to "new_message"
            )
        )

        firebasePushNotificationService.sendNotification(notification)
    }
}