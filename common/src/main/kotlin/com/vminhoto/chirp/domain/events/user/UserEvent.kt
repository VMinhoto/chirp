package com.vminhoto.chirp.domain.events.user

import com.vminhoto.chirp.domain.events.ChirpEvent
import com.vminhoto.chirp.domain.type.UserId
import java.time.Instant
import java.util.UUID

/**
 * Sealed class representing the possible UserEvents that can exist. Extends ChirpEvent
 * @property eventId represents the ID of the event.
 * @property eventId ID of the event. Default value is random UUID.
 * @property occurredAt Instant object representing the time of which the event was fired. Default value is USER_EXCHANGE
 * @property exchange Representation of the queue that reroutes the event to the consumer. Default value is now()
 */
sealed class UserEvent(
    override val eventId: String = UUID.randomUUID().toString(),
    override val exchange: String = UserEventConstants.USER_EXCHANGE,
    override val occurredAt: Instant = Instant.now(),
): ChirpEvent {

    /**
     * Data Class representing the user creation event. Implements ChirpEvent
     * @property userId UUID of the user associated to the event
     * @property email email of the user associated to the event
     * @property username username of the user associated to the event
     * @property verificationToken
     * @property eventKey key related to the event type. Default: USER_CREATED_KEY
     *
     */
    data class Created(
        val userId: UserId,
        val email: String,
        val username: String,
        val verificationToken: String,
        override val eventKey: String = UserEventConstants.USER_CREATED_KEY
    ): UserEvent(), ChirpEvent

    /**
     * Data Class representing the user creation event. Implements ChirpEvent
     * @property userId UUID of the user associated to the event
     * @property email email of the user associated to the event
     * @property username username of the user associated to the event
     * @property eventKey key related to the event type. Default: USER_VERIFIED
     *
     */
    data class Verified(
        val userId: UserId,
        val email: String,
        val username: String,
        override val eventKey: String = UserEventConstants.USER_VERIFIED
    ): UserEvent(), ChirpEvent

    /**
     * Data Class representing the user creation event. Implements ChirpEvent
     * @property userId UUID of the user associated to the event
     * @property email email of the user associated to the event
     * @property username username of the user associated to the event
     * @property verificationToken
     * @property eventKey key related to the event type. Default: USER_VERIFIED
     *
     */
    data class RequestResendVerification(
        val userId: UserId,
        val email: String,
        val username: String,
        val verificationToken: String,
        override val eventKey: String = UserEventConstants.USER_REQUEST_RESEND_VERIFICATION
    ): UserEvent(), ChirpEvent

    /**
     * Data Class representing the user creation event. Implements ChirpEvent
     * @property userId UUID of the user associated to the event
     * @property email email of the user associated to the event
     * @property username username of the user associated to the event
     * @property passwordResetToken
     * @property eventKey key related to the event type. Default: USER_VERIFIED
     *
     */
    data class RequestResetPassword(
        val userId: UserId,
        val email: String,
        val username: String,
        val passwordResetToken: String,
        val expiresInMinutes: Long,
        override val eventKey: String = UserEventConstants.USER_REQUEST_RESET_PASSWORD
    ): UserEvent(), ChirpEvent

}