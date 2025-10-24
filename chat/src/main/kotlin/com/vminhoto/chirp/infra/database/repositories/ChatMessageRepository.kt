package com.vminhoto.chirp.infra.database.repositories

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.ChatMessageId
import com.vminhoto.chirp.infra.database.entities.ChatMessageEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

/**
 * Repository interface for Chat Messages
 */
interface ChatMessageRepository: JpaRepository<ChatMessageEntity, ChatMessageId> {

    /**
     * Function to query our Database for chat messages by ChatId before a certain timestamp.
     * @param chatId the Id of the chat.
     * @param before the latest time we want for the list of messages.
     * @param pageable
     * @return Slice<ChatMessageEntity>
     */
    @Query("""
        SELECT m
        FROM ChatMessageEntity m
        WHERE m.chatId = :chatId
        AND m.createdAt < :before
        ORDER BY m.createdAt DESC
    """)
    fun findByChatIdBefore(
        chatId: ChatId,
        before: Instant,
        pageable: Pageable
    ): Slice<ChatMessageEntity>

    /**
     * Function that finds the Latest Message in each Chat.
     * @param chatIds Set of Chat Ids we want the latest message for
     * @return List<ChatMessageEntity> The latest message for each Chat.
     */
    @Query("""
        SELECT m
        FROM ChatMessageEntity m
        LEFT JOIN FETCH m.sender
        WHERE m.chatId IN :chatIds
        AND (m.createdAt, m.id) = (
            SELECT m2.createdAt , m2.id
            FROM ChatMessageEntity m2
            WHERE m2.chatId = m.chatId
            ORDER BY m2.createdAt DESC
            LIMIT 1
        )
    """)
    fun findLatestMessagesByChatIds(
        chatIds: Set<ChatId>
    ): List<ChatMessageEntity>
}