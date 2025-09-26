import gradle.kotlin.dsl.accessors._1a4939bdc5f6e939f50d04029c2e9574.allOpen
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    id("chirp.spring-boot-service")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}