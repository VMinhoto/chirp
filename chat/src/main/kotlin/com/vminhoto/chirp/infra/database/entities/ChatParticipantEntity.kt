package com.vminhoto.chirp.infra.database.entities

import com.vminhoto.chirp.domain.type.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

/**
 * Class defining the entity for Chat participant.
 *  @property userId The UserId of the Participant. Primary key.
 *  @property username The ChatParticipant's username. Not nullable and unique.
 *  @property email The ChatParticipant's email. Not nullable and unique.
 *  @property profilePictureUrl The URL for the profile picture of the ChatParticipant if it exists.
 *  @property createdAt The time the ChatParticipant was created. Generated at creation.
 */
@Entity
@Table(
    name = "chat_participants",
    schema = "chat_service",
    indexes = [
        Index(name = "idx_chat_participant_username", columnList = "username"),
        Index(name = "idx_chat_participant_email", columnList = "email"),
    ]
)
class ChatParticipantEntity(
    @Id
    var userId: UserId,
    @Column(nullable = false, unique = true)
    var username: String,
    @Column(nullable = false, unique = true)
    var email: String,
    @Column(nullable = true)
    var profilePictureUrl: String? = null,
    @CreationTimestamp
    var createdAt: Instant = Instant.now()
)