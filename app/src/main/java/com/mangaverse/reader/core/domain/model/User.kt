package com.mangaverse.reader.core.domain.model

/**
 * Domain model representing a user
 *
 * @property username The username of the user
 * @property isAuthenticated Whether the user is authenticated
 */
data class User(
    val username: String,
    val isAuthenticated: Boolean
)