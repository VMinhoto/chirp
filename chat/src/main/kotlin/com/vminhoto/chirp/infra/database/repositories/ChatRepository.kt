package com.vminhoto.chirp.infra.database.repositories

import com.vminhoto.chirp.domain.type.ChatId
import com.vminhoto.chirp.domain.type.UserId
import com.vminhoto.chirp.infra.database.entities.ChatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Repository interface for Chat participants.
 */
interface ChatRepository: JpaRepository<ChatEntity, ChatId> {

    /**
     * Function that finds a chat by [ChatId] and [UserId].
     * @param id Id of the chat.
     * @param userId Id of the User.
     * @return ChatEntity returns the resulting [ChatEntity]
     */
    @Query("""
        SELECT c
        FROM ChatEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE c.id = :id
        AND EXISTS (
            SELECT 1
            FROM c.participants p
            WHERE p.userId = :userId
        )
    """)
    fun findChatBy(id: ChatId, userId: UserId): ChatEntity?

    /**
     * Function that finds a list of chats belonging to a Chat Participant by [UserId].
     * @param userId Id of the User.
     * @return List<ChatEntity> returns a list of [ChatEntity] objects that that user participates in.
     */
    @Query("""
        SELECT c
        FROM ChatEntity c
        LEFT JOIN FETCH c.participants
        LEFT JOIN FETCH c.creator
        WHERE EXISTS (
            SELECT 1
            FROM c.participants p
            WHERE p.userId = :userId
        )
    """)
    fun findAllByUserId(userId: UserId): List<ChatEntity>
}