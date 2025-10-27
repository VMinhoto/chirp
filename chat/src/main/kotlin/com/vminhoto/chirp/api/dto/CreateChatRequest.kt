package com.vminhoto.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.vminhoto.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

/**
 * Data class holding the data about the request about creating a chat.
 * @param otherUsersIds the Ids of the Chat Participants involved in the Chat. Must be at least 1.
 */
data class CreateChatRequest(
    @field:Size(
        min = 1,
        message = "The chat must have at least 2 unique participants"
    )
    @JsonProperty("otherUserIds")
    val otherUsersIds: List<UserId>
)
