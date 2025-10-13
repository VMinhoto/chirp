package com.vminhoto.chirp.domain.exception

class RateLimitException(
    val resetsInSeconds: Long
): RuntimeException(
    "RateLimit exceeded. Please try again in $resetsInSeconds seconds."
)