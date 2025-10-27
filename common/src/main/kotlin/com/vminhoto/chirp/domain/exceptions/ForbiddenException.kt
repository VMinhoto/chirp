package com.vminhoto.chirp.domain.exceptions

import java.lang.RuntimeException

/**
 * Exception to be thrown on general forbidden actions.
 */
class ForbiddenException: RuntimeException("You are not allowed to do that") {
}