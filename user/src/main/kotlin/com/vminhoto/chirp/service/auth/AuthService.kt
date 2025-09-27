package com.vminhoto.chirp.service.auth

import com.vminhoto.chirp.api.dto.AuthenticatedUserDto
import com.vminhoto.chirp.domain.exception.InvalidCredentialsException
import com.vminhoto.chirp.domain.exception.UserAlreadyExistsException
import com.vminhoto.chirp.domain.exception.UserNotFoundException
import com.vminhoto.chirp.domain.model.AuthenticatedUser
import com.vminhoto.chirp.domain.model.User
import com.vminhoto.chirp.domain.model.UserId
import com.vminhoto.chirp.infra.database.entities.RefreshTokenEntity
import com.vminhoto.chirp.infra.database.entities.UserEntity
import com.vminhoto.chirp.infra.database.mappers.toUser
import com.vminhoto.chirp.infra.database.repositories.RefreshTokenRepository
import com.vminhoto.chirp.infra.database.repositories.UserRepository
import com.vminhoto.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64


@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
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

    fun login(
        email: String,
        password: String
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim())
            ?: throw InvalidCredentialsException()

        if(!passwordEncoder.matches(password, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        // TODO: Check for verified email

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()
    }

    private fun storeRefreshToken(userId: UserId, token: String){
        val hashed = hashToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String) : String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hasBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hasBytes)
    }

}