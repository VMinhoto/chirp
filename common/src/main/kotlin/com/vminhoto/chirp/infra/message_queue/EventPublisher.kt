package com.vminhoto.chirp.infra.message_queue

import com.vminhoto.chirp.domain.events.ChirpEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * Publisher class that publishes and event to a RabbitMQ queue
 *
 * @property rabbitTemplate Template that handles serialization between JSON and our domain modules
 */
@Component
class EventPublisher(
    private val rabbitTemplate: RabbitTemplate,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Function that publishes and event of type ChirpEvent
     */
    fun <T: ChirpEvent> publish(event: T) {
        try {
            rabbitTemplate.convertAndSend(
                event.exchange,
                event.eventKey,
                event
            )
            logger.info("Successfully published event: ${event.eventKey}")
        } catch (e: Exception) {
            logger.error("Error publishing event", e)
        }
    }
}