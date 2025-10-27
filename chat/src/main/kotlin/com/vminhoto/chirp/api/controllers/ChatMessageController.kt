package com.vminhoto.chirp.api.controllers

import com.vminhoto.chirp.api.util.requestUserId
import com.vminhoto.chirp.domain.type.ChatMessageId
import com.vminhoto.chirp.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller to handle ChatMessages endpoint. Only delete message is here as send message will be handled by websockets
 */
@RestController
@RequestMapping("/api/messages")
class ChatMessageController(private val chatMessageService: ChatMessageService) {

    /**
     * Delete message endpoint.
     * @param messageId Id from the message. Comes from the URL.
     */
    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable("messageId") messageId: ChatMessageId
    ) {
        chatMessageService.deleteMessage(messageId, requestUserId)
    }
}