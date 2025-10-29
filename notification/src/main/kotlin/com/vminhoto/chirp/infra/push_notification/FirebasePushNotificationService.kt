package com.vminhoto.chirp.infra.push_notification

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MessagingErrorCode
import com.google.firebase.messaging.Notification
import com.vminhoto.chirp.domain.model.DeviceToken
import com.vminhoto.chirp.domain.model.PushNotification
import com.vminhoto.chirp.domain.model.PushNotificationSendResult
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service

/**
 * Service to handle firebase configuration and internal operation.
 * @param credentialsPath path for the firebase credentials.
 * @param resourceLoader Loader to load credentials from file.
 */
@Service
class FirebasePushNotificationService(
    @param:Value("\${firebase.credentials-path}")
    private val credentialsPath: String,
    private val resourceLoader: ResourceLoader
) {

    private val logger = LoggerFactory.getLogger(FirebasePushNotificationService::class.java)

    /**
     * This function runs on the Service constructor and initializes the firebase app with the given credentials
     */
    @PostConstruct
    fun initialize() {
        try {
            val serviceAccount = resourceLoader.getResource(credentialsPath)

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.inputStream))
                .build()

            FirebaseApp.initializeApp(options)
            logger.info("Firebase Admin SDK initialized successfully.")
        } catch (e: Exception) {
            logger.error("Error initializing Firebase Admin SDK", e)
        }
    }

    /**
     * Function to check if a firebase token send by a client is valid.
     * @param token the token sent by the Client
     * @return true if valid, or false if invalid.
     */
    fun isValidToken(token: String): Boolean{
        val message = Message.builder()
            .setToken(token)
            .build()

        return try {
            FirebaseMessaging.getInstance().send(message, true)
            true

        } catch (e: FirebaseMessagingException) {
            logger.warn("Failed to validate Firebase token", e)
            false
        }
    }

    /**
     * Function to send a push notification to all respective clients
     * @param notification
     * @return a [PushNotificationSendResult] representing successes and failures.
     */
    fun sendNotification(notification: PushNotification): PushNotificationSendResult {
        val messages = notification.recipients.map { recipient ->
            Message.builder()
                .setToken(recipient.token)
                .setNotification(
                    Notification.builder()
                        .setTitle(notification.title)
                        .setBody(notification.message)
                        .build()
                )
                .apply {
                    notification.data.forEach { (key, value) ->
                        putData(key, value)
                    }

                    when(recipient.platform) {
                        DeviceToken.Platform.ANDROID -> {
                            setAndroidConfig(
                                AndroidConfig.builder()
                                    .setPriority(AndroidConfig.Priority.HIGH)
                                    .setCollapseKey(notification.chatId.toString())
                                    .setRestrictedPackageName("com.vminhoto.chirp")
                                    .build()
                            )
                        }
                        DeviceToken.Platform.IOS -> {
                            setApnsConfig(
                                ApnsConfig.builder()
                                    .setAps(
                                        Aps.builder()
                                            .setSound("default")
                                            .setThreadId(notification.chatId.toString())
                                            .build()
                                    )
                                    .build()
                            )

                        }
                    }
                }
                .build()
        }

        return FirebaseMessaging
            .getInstance()
            .sendEach(messages)
            .toSendResult(notification.recipients)
    }

    /**
     * Extension function to convert FireBase batch responses into [PushNotificationSendResult]
     * @param allDeviceTokens Device tokens
     * @return [PushNotificationSendResult]
     */
    private fun BatchResponse.toSendResult(
        allDeviceTokens: List<DeviceToken>
    ): PushNotificationSendResult {
        val succeeded = mutableListOf<DeviceToken>()
        val temporaryFailures = mutableListOf<DeviceToken>()
        val permanentFailures = mutableListOf<DeviceToken>()

        responses.forEachIndexed { index, sendResponse ->
            val deviceToken = allDeviceTokens[index]
            if(sendResponse.isSuccessful) {
                succeeded.add(deviceToken)
            } else {
                val errorCode = sendResponse.exception?.messagingErrorCode

                logger.warn("Failed to send push notification to token ${deviceToken.token}: $errorCode")

                when(errorCode){
                    MessagingErrorCode.UNREGISTERED,
                    MessagingErrorCode.SENDER_ID_MISMATCH,
                    MessagingErrorCode.INVALID_ARGUMENT,
                    MessagingErrorCode.THIRD_PARTY_AUTH_ERROR -> {
                        permanentFailures.add(deviceToken)
                    }
                    MessagingErrorCode.INTERNAL,
                    MessagingErrorCode.QUOTA_EXCEEDED,
                    MessagingErrorCode.UNAVAILABLE,
                    null ->  {
                        temporaryFailures.add(deviceToken)
                    }
                }
            }
        }
        logger.debug("Push notifications sent. Succeeded: ${succeeded.size}, " +
        "temporary failures ${temporaryFailures.size}, permanent failures: ${permanentFailures.size}")

        return PushNotificationSendResult(
            succeeded = succeeded.toList(),
            temporaryFailures = temporaryFailures.toList(),
            permanentFailures = permanentFailures.toList()
        )
    }
}