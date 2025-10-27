package com.vminhoto.chirp.api.controllers

import com.vminhoto.chirp.api.dto.AddParticipantToChatDto
import com.vminhoto.chirp.api.dto.ChatDto
import com.vminhoto.chirp.api.dto.CreateChatRequest
import com.vminhoto.chirp.api.mappers.toChatDto
import com.vminhoto.chirp.api.util.requestUserId
import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
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

    /**
     * Endpoint to add a user to a chat.
     * @param chatId The Id of the chat. Comes from the path of the request
     * @param body body of the request where it contains the info of the participant to add.
     * @return A chat DTO representing the updated chat.
     */
    @PostMapping("/{chatId}/add")
    fun addChatParticipants(
        @PathVariable chatId: ChatId,
        @Valid @RequestBody body: AddParticipantToChatDto
    ): ChatDto {
        return chatService.addParticipantsToChat(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet()
        ).toChatDto()
    }

    /**
     * Endpoint to leave a chat.
     * @param chatId The Id of the chat. Comes from the path of the request
     */
    @DeleteMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId,
    ) {
        chatService.removeParticipantFromChat(
            chatId = chatId,
            userId = requestUserId
        )
    }
}