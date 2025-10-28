package com.vminhoto.chirp.service

import com.vminhoto.chirp.domain.event.ProfilePictureUpdatedEvent
import com.vminhoto.chirp.domain.exception.ChatParticipantNotFoundException
import com.vminhoto.chirp.domain.models.ProfilePictureUploadCredentials
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.repositories.ChatParticipantRepository
import com.vminhoto.chirp.infra.storage.SupabaseStorageService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * Service that defines business logic about Profile Pictures
 * @param supabaseStorageService
 * @param chatParticipantRepository
 * @param applicationEventPublisher
 */
@Service
class ProfilePictureService(
    private val supabaseStorageService: SupabaseStorageService,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val logger = LoggerFactory.getLogger(ProfilePictureService::class.java)
    /**
     * Function to generate the Upload Credentials Object
     * @param userId the Id of the user.
     * @param mimeType the type of the media.
     * @return The profile picture upload credentials.
     */
    fun generateUploadCredentials(
        userId: UserId,
        mimeType: String,
    ): ProfilePictureUploadCredentials {
        return supabaseStorageService.generateSignedUploadUrl(
            userId = userId,
            mimeType = mimeType
        )
    }

    /**
     * Logic on how to delete profile pictures. Gets the Chat Participant, then the profile picture url
     * sets it to null then calls the delete fill from supabase storage service. Publishes a
     * [ProfilePictureUpdatedEvent]
     * @param userId the Id of the User.
     * @throws ChatParticipantNotFoundException if the Chat Participant is not found.
     */
    @Transactional
    fun deleteProfilePicture(userId: UserId) {
        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        participant.profilePictureUrl?.let { url ->
            chatParticipantRepository.save(
                participant.apply { profilePictureUrl = null }
            )

            supabaseStorageService.deleteFile(url)

            applicationEventPublisher.publishEvent(
                ProfilePictureUpdatedEvent(
                    userId = userId,
                    newUrl = null
                )
            )
        }
    }

    /**
     * Logic on how to update profile pictures. Gets the Chat Participant,updates the database with the new URL.
     * Publishes  [ProfilePictureUpdatedEvent]
     * @param userId the Id of the User.
     * @param publicUrl the URL of the picture.
     * @throws ChatParticipantNotFoundException if the Chat Participant is not found.
     */
    fun confirmProfilePictureUpload(userId: UserId, publicUrl: String) {
        val participant = chatParticipantRepository.findByIdOrNull(userId)
            ?: throw ChatParticipantNotFoundException(userId)

        val oldUrl = participant.profilePictureUrl

        chatParticipantRepository.save(
            participant.apply { profilePictureUrl = publicUrl }
        )

        try {
            oldUrl?.let {
                supabaseStorageService.deleteFile(oldUrl)
            }

        } catch (e: Exception) {
            logger.warn("Deleting old profile picture for $userId failed", e)
        }


        applicationEventPublisher.publishEvent(
            ProfilePictureUpdatedEvent(
                userId = userId,
                newUrl = publicUrl
            )
        )
    }
}