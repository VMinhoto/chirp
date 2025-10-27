package com.vminhoto.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.vminhoto.chirp.domain.type.UserId
import jakarta.validation.constraints.Size

/**
 * DTO for the add participant API
 * @param userIds The Ids of the users to add to the chat.
 */
data class AddParticipantToChatDto(
    @field:Size(min = 1)
    @JsonProperty("userIds")
    val userIds: List<UserId>
)