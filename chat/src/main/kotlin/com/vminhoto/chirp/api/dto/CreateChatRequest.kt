package com.vminhoto.chirp.api.dto

import com.vminhoto.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

/**
 * TODO add documentation
 */
data class CreateChatRequest(
    @field:Size(
        min = 1,
        message = "The chat must have at least 2 unique participants"
    )
    val otherUsersIds: List<UserId>
)
