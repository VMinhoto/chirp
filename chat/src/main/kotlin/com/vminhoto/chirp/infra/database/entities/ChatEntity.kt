package com.vminhoto.chirp.infra.database.entities

import com.vminhoto.chirp.domain.type.ChatId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

/**
 * Class defining the entity for Chat.
 * @param id the id of the chat.
 * @param creator the creator of the chat. Relates to the [ChatParticipantEntity] using the user_id has a Join.
 * @param participants A Set of [ChatParticipantEntity] representing the participants that are in the Chat.
 * @param createdAt the [Instant] that the Chat was created.
 */
@Entity
@Table(
    name = "chats",
    schema = "chat_service",
)
class ChatEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: ChatId? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false
    )
    var creator: ChatParticipantEntity,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "chat_participants_cross_ref",
        schema = "chat_service",
        joinColumns = [JoinColumn(name = "chat_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")],
        indexes = [
            // Answers efficiently:
            // Who is in chat X?
            Index(
                name = "idx_chat_participant_chat_id_user_id",
                columnList = "chat_id,user_id",
                unique = true
            ),
            // Answers efficiently:
            // What chats is user X in?
            Index(
                name = "idx_chat_participant_user_id_chat_id",
                columnList = "user_id,chat_id",
                unique = true
            )
        ]
    )
    var participants: Set<ChatParticipantEntity> = emptySet(),
    @CreationTimestamp
    var createdAt: Instant = Instant.now(),
)