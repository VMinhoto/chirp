package com.vminhoto.chirp.infra.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

// Resolves configuration from application.yml, application-dev.yml and application-prod.yml
@Configuration
@ConfigurationProperties(prefix = "nginx")
data class NginxConfig(
    var trustedIps: List<String> = emptyList(),
    var requireProxy: Boolean = true
)
