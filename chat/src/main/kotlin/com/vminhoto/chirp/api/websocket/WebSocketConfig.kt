package com.vminhoto.chirp.api.websocket

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

/**
 * Websocket Config to register the websocket handler.
 * @param handler the ChatWebSocketHandler
 * @param allowedOrigins allowed origins (defined in the config)
 **/
@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val handler: ChatWebSocketHandler,
    @param:Value("\${chirp.web-socket.allowed-origin}")
    private val allowedOrigin: String
): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(handler, "/ws/chat")
            .setAllowedOrigins(
                allowedOrigin
            )
    }
}