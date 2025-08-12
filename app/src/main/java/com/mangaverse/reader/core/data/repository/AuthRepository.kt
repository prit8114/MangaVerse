package com.mangaverse.reader.core.data.repository

import com.mangaverse.reader.core.data.network.api.MangaDexApi
import com.mangaverse.reader.core.data.network.model.LoginRequest
import com.mangaverse.reader.core.data.network.model.LogoutRequest
import com.mangaverse.reader.core.data.network.model.RefreshTokenRequest
import com.mangaverse.reader.core.domain.Result
import com.mangaverse.reader.core.domain.model.User
import com.mangaverse.reader.core.security.SecurityManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for authentication operations
 */
@Singleton
class AuthRepository @Inject constructor(
    private val mangaDexApi: MangaDexApi,
    private val securityManager: SecurityManager
) {

    companion object {
        private const val SESSION_TOKEN_KEY = "session_token"
        private const val REFRESH_TOKEN_KEY = "refresh_token"
        private const val USERNAME_KEY = "username"
    }

    /**
     * Login to MangaDex
     */
    suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = mangaDexApi.login(LoginRequest(username, password))
            
            // Store tokens securely
            securityManager.storeSecureValue(SESSION_TOKEN_KEY, response.token.session)
            securityManager.storeSecureValue(REFRESH_TOKEN_KEY, response.token.refresh)
            securityManager.storeSecureValue(USERNAME_KEY, username)
            
            Result.Success(User(username, true))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Logout from MangaDex
     */
    suspend fun logout(): Result<Unit> {
        return try {
            val refreshToken = securityManager.getSecureValue(REFRESH_TOKEN_KEY)
            if (refreshToken != null) {
                try {
                    mangaDexApi.logout(LogoutRequest(refreshToken))
                } catch (e: Exception) {
                    // Ignore errors during logout API call
                }
            }
            
            // Clear stored tokens
            securityManager.removeSecureValue(SESSION_TOKEN_KEY)
            securityManager.removeSecureValue(REFRESH_TOKEN_KEY)
            securityManager.removeSecureValue(USERNAME_KEY)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Refresh authentication token
     */
    suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = securityManager.getSecureValue(REFRESH_TOKEN_KEY)
                ?: return Result.Error(Exception("No refresh token found"))
            
            val response = mangaDexApi.refreshToken(RefreshTokenRequest(refreshToken))
            
            // Store new tokens
            securityManager.storeSecureValue(SESSION_TOKEN_KEY, response.token.session)
            securityManager.storeSecureValue(REFRESH_TOKEN_KEY, response.token.refresh)
            
            Result.Success(response.token.session)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Get current session token
     */
    fun getSessionToken(): String? {
        return securityManager.getSecureValue(SESSION_TOKEN_KEY)
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getSessionToken() != null
    }

    /**
     * Get current user
     */
    fun getCurrentUser(): User? {
        val username = securityManager.getSecureValue(USERNAME_KEY) ?: return null
        return User(username, true)
    }
}