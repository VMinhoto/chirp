package com.vminhoto.chirp.domain.exception

import java.lang.RuntimeException

class InvalidCredentialsException: RuntimeException(
    "The entered credentials aren't valid"
)