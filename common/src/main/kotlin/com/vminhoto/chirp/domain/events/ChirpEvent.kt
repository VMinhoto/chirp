package com.vminhoto.chirp.domain.events

import java.time.Instant

/**
 * Chirp Event is the interface that will represent what every chirp event will have.
 *
 * @property eventId ID of the event
 * @property eventKey Key representing what type of event that is
 * @property occurredAt Instant object representing the time of which the event was fired
 * @property exchange Representation of the queue that reroutes the event to the consumer.
 *
 */
interface ChirpEvent {
    val eventId: String
    val eventKey: String
    val occurredAt: Instant
    val exchange: String
}