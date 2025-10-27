package com.vminhoto.chirp.api.exception_handling

import com.vminhoto.chirp.domain.exception.ChatNotFoundException
import com.vminhoto.chirp.domain.exception.ChatParticipantNotFoundException
import com.vminhoto.chirp.domain.exception.InvalidChatSizeException
import com.vminhoto.chirp.domain.exception.MessageNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Exception Handle to handle custom exceptions.
 */
@RestControllerAdvice
class ChatExceptionHandler {

    /**
     * Handle [ChatNotFoundException], [MessageNotFoundException] and [ChatNotFoundException]. Returns Not Found. With
     * the message from the exception.
     * @param e Exception
     */
    @ExceptionHandler(
        ChatNotFoundException::class,
        MessageNotFoundException::class,
        ChatParticipantNotFoundException::class,
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onForbidden(e: Exception) = mapOf(
        "code" to "NOT_FOUND",
        "message" to e.message
    )
    /**
     * Handle [InvalidChatSizeException]. Returns Bad Request INVALID_CHAT_SIZE. With the message from the exception.
     * @param e Exception
     */
    @ExceptionHandler(InvalidChatSizeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun onForbidden(e: InvalidChatSizeException) = mapOf(
        "code" to "INVALID_CHAT_SIZE",
        "message" to e.message
    )


}