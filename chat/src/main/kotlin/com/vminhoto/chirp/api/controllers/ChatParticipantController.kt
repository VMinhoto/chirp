package com.vminhoto.chirp.api.controllers

import com.vminhoto.chirp.api.dto.ChatParticipantDto
import com.vminhoto.chirp.api.dto.ConfirmProfilePictureRequest
import com.vminhoto.chirp.api.dto.PictureUploadResponse
import com.vminhoto.chirp.api.mappers.toChatParticipantDto
import com.vminhoto.chirp.api.mappers.toResponse
import com.vminhoto.chirp.api.util.requestUserId
import com.vminhoto.chirp.service.ChatParticipantService
import com.vminhoto.chirp.service.ProfilePictureService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

/**
 * Controller to handle chatParticipants APIs.
 * @param chatParticipantService chat participant service.
 */
@RestController
@RequestMapping("/api/participants")
class ChatParticipantController(
    private val chatParticipantService: ChatParticipantService,
    private val profilePictureService: ProfilePictureService
) {

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

    /**
     * Endpoint upload a picture. It generates the credentials for the client to upload it to supabase.
     * @param mimeType The type of media.
     * @return Picture upload response with info to upload the picture.
     */
    @PostMapping("/profile-picture-upload")
    fun getProfilePictureUploadUrl(
        @RequestParam mimeType: String
    ): PictureUploadResponse {
        return profilePictureService.generateUploadCredentials(
            userId = requestUserId,
            mimeType = mimeType
        ).toResponse()
    }

    /**
     * Endpoint when the client confirms the upload of the picture
     * @param body with the publicURL
     */
    @PostMapping("/confirm-profile-picture")
    fun confirmProfilePictureUploadUrl(
        @Valid @RequestBody body: ConfirmProfilePictureRequest
    ) {
        return profilePictureService.confirmProfilePictureUpload(
            userId = requestUserId,
            publicUrl = body.publicUrl
        )
    }

    /**
     * Endpoint when the user deletes the profile picture.
     */
    @DeleteMapping("/profile-picture")
    fun deleteProfilePicture() {
        profilePictureService.deleteProfilePicture(
            userId = requestUserId
        )
    }
}