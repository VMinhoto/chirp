package com.vminhoto.chirp.api.controllers

import com.vminhoto.chirp.api.dto.ChatDto
import com.vminhoto.chirp.api.dto.CreateChatRequest
import com.vminhoto.chirp.api.mappers.toChatDto
import com.vminhoto.chirp.api.util.requestUserId
import com.vminhoto.chirp.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller to handle chat APIs.
 * @param chatService chat service.
 */
@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {
    /**
     * Endpoint to create a chat. The creator ID is always the authenticated ChatParticipant.
     * @param body A [CreateChatRequest] containing the participants.
     */
    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUsersIds.toSet()
        ).toChatDto()
    }
}