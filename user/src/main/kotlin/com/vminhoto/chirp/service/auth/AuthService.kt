package com.vminhoto.chirp.service.auth

import com.vminhoto.chirp.api.dto.AuthenticatedUserDto
import com.vminhoto.chirp.domain.exception.UserAlreadyExistsException
import com.vminhoto.chirp.domain.model.User
import com.vminhoto.chirp.infra.database.entities.UserEntity
import com.vminhoto.chirp.infra.database.mappers.toUser
import com.vminhoto.chirp.infra.database.repositories.UserRepository
import com.vminhoto.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim(),
        )

        if(user != null) {
            throw UserAlreadyExistsException()
        }

        val savedUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password)!!
            )
        ).toUser()

        return savedUser

    }

}