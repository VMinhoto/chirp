package com.vminhoto.chirp.domain.model

/**
 * Data class representing the domain result of push sending push notifications model.
 * @param succeeded List of Devices that the notification succeeded.
 * @param temporaryFailures List of Devices that the notification temporarily failed.
 * @param permanentFailures List of Devices that the notification permanently failed.
 */
data class PushNotificationSendResult(
    val succeeded: List<DeviceToken>,
    val temporaryFailures: List<DeviceToken>,
    val permanentFailures: List<DeviceToken>,
)
