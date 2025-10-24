package com.vminhoto.chirp.domain.exception

import com.vminhoto.chirp.domain.type.UserId

/**
 * Class to define a custom  ChatParticipantNotFoundException with a custom message. Should fire when there is not a
 * user found after using the find user by Id query.
 * @property id the id of the user not found.
 */
class ChatParticipantNotFoundException(
    private val id: UserId
): RuntimeException(
    "The chat participant with the ID $id was not found."
)