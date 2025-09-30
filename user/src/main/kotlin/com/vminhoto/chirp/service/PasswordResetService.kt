package com.vminhoto.chirp.service

import com.vminhoto.chirp.domain.exception.InvalidCredentialsException
import com.vminhoto.chirp.domain.exception.InvalidTokenException
import com.vminhoto.chirp.domain.exception.SamePasswordException
import com.vminhoto.chirp.domain.exception.UserNotFoundException
import com.vminhoto.chirp.domain.model.UserId
import com.vminhoto.chirp.infra.database.entities.PasswordResetTokenEntity
import com.vminhoto.chirp.infra.database.repositories.PasswordResetTokenRepository
import com.vminhoto.chirp.infra.database.repositories.RefreshTokenRepository
import com.vminhoto.chirp.infra.database.repositories.UserRepository
import com.vminhoto.chirp.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

open class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
    @param:Value("\$chirp.email.reset-password.expiry-minutes}")
    private val expiryMinutes: Long
) {
    @Transactional
    open fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)
        )
        passwordResetTokenRepository.save(token)

        // TODO: Inform notification service about password reset trigger to send email
    }

    @Transactional
    open fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        if(resetToken.isUsed) {
            throw InvalidTokenException("Password reset token is already used")
        }

        if (resetToken.isExpired){
            throw InvalidTokenException("Password reset token is expired")
        }

        val user = resetToken.user

        if(passwordEncoder.matches(newPassword, user.hashedPassword)) {
            throw SamePasswordException()
        }

        val hashedNewPassword = passwordEncoder.encode(newPassword)!!
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedNewPassword
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    open fun changePassword(
        userId: UserId,
        newPassword: String,
        oldPassword: String
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        if(!passwordEncoder.matches(oldPassword, user.hashedPassword)){
            throw InvalidCredentialsException()
        }

        if (newPassword == oldPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)!!

        userRepository.save(
            user.apply {
                this.hashedPassword = newHashedPassword
            }
        )

    }

    @Scheduled(cron = "0 0 3 * * *")
    open fun cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }

}