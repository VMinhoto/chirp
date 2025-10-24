package com.vminhoto.chirp.domain.exception

import java.lang.RuntimeException

/**
 * Class to define a custom exception InvalidChatSizeException with a custom message. Should fire when a user tries to
 * create a chat with less then 2 People.
 */
class InvalidChatSizeException: RuntimeException(
    "There must be at least 2 unique participants to chat."
)