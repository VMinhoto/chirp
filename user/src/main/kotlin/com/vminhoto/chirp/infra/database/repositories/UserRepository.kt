package com.vminhoto.chirp.infra.database.repositories

import com.vminhoto.chirp.domain.model.UserId
import com.vminhoto.chirp.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity, UserId> {
    fun findByEmail(email: String): UserEntity?
    fun findByEmailOrUsername(email: String, username: String): UserEntity?
}