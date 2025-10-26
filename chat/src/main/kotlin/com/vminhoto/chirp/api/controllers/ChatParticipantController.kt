package com.vminhoto.chirp.api.controllers

import com.vminhoto.chirp.api.dto.ChatParticipantDto
import com.vminhoto.chirp.api.mappers.toChatParticipantDto
import com.vminhoto.chirp.api.util.requestUserId
import com.vminhoto.chirp.service.ChatParticipantService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * Controller to handle chatParticipants APIs.
 * @param chatParticipantService chat participant service.
 */
@RestController
@RequestMapping("/api/chat/participants")
class ChatParticipantController(private val chatParticipantService: ChatParticipantService) {

    /**
     * Endpoint to get a Chat participant by username or email. If no query is passed use the current logged user
     * userId.
     * @param query the username or email of the user. Can be null.
     * @return ChatParticipantDto
     * @throws ResponseStatusException Not found if chat participant does not exist
     */
    @GetMapping
    fun getChatParticipantsByUsernameOrEmail(
        @RequestParam(required = false) query: String?
    ): ChatParticipantDto {
        val participant = if(query == null) {
            chatParticipantService.findChatParticipantById(requestUserId)
        } else {
            chatParticipantService.findChatParticipantByUsernameOrEmail(query)
        }

        return participant?.toChatParticipantDto()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}