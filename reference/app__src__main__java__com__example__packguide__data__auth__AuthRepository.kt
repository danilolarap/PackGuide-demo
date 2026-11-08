package com.example.packguide.data.auth

import android.database.sqlite.SQLiteConstraintException
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface AuthResult {
    data object Success : AuthResult
    data object InvalidCredentials : AuthResult
    data object InvalidInput : AuthResult
    data object UsernameTaken : AuthResult
    data object StorageError : AuthResult
}

class AuthRepository(private val userAccountDao: UserAccountDao) {
    suspend fun register(username: String, password: String): AuthResult {
        val normalizedUsername = normalizeUsername(username)
        if (AuthValidation.usernameError(normalizedUsername) != null ||
            AuthValidation.passwordError(password) != null
        ) return AuthResult.InvalidInput
        return runCatching {
            if (userAccountDao.findByUsername(normalizedUsername) != null) {
                return@runCatching AuthResult.UsernameTaken
            }
            val salt = ByteArray(SALT_SIZE).also(SecureRandom()::nextBytes)
            userAccountDao.insert(
                UserAccount(
                    username = normalizedUsername,
                    passwordHash = withContext(Dispatchers.Default) {
                        derivePasswordHash(password, salt)
                    },
                    passwordSalt = Base64.encodeToString(salt, Base64.NO_WRAP)
                )
            )
            AuthResult.Success
        }.getOrElse { error ->
            if (error is CancellationException) throw error
            if (error is SQLiteConstraintException) AuthResult.UsernameTaken
            else AuthResult.StorageError
        }
    }

    suspend fun login(username: String, password: String): AuthResult {
        val normalizedUsername = normalizeUsername(username)
        val account = runCatching { userAccountDao.findByUsername(normalizedUsername) }
            .getOrElse {
                if (it is CancellationException) throw it
                return AuthResult.StorageError
            }
            ?: return AuthResult.InvalidCredentials

        val salt = runCatching { Base64.decode(account.passwordSalt, Base64.NO_WRAP) }
            .getOrElse { return AuthResult.StorageError }
        val actual = runCatching {
            withContext(Dispatchers.Default) { derivePasswordHash(password, salt) }
        }
            .getOrElse {
                if (it is CancellationException) throw it
                return AuthResult.StorageError
            }
        return if (MessageDigest.isEqual(actual.toByteArray(), account.passwordHash.toByteArray())) {
            AuthResult.Success
        } else {
            AuthResult.InvalidCredentials
        }
    }

    private fun derivePasswordHash(password: String, salt: ByteArray): String {
        val passwordChars = password.toCharArray()
        val spec = javax.crypto.spec.PBEKeySpec(passwordChars, salt, ITERATIONS, HASH_BITS)
        return try {
            Base64.encodeToString(
                javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).encoded,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
            passwordChars.fill('\u0000')
        }
    }

    private fun normalizeUsername(username: String): String =
        username.trim().lowercase(Locale.ROOT)

    private companion object {
        const val SALT_SIZE = 16
        const val ITERATIONS = 120_000
        const val HASH_BITS = 256
    }
}
