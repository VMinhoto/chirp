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
 * TODO add documentation
 */
@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {
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