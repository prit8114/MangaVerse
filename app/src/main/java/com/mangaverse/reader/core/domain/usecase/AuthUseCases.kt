package com.mangaverse.reader.core.domain.usecase

import com.mangaverse.reader.core.data.repository.AuthRepository
import com.mangaverse.reader.core.domain.CompletableUseCase
import com.mangaverse.reader.core.domain.NoParamUseCase
import com.mangaverse.reader.core.domain.UseCase
import com.mangaverse.reader.core.domain.Result
import com.mangaverse.reader.core.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

/**
 * Container for all authentication-related use cases
 */
data class AuthUseCases(
    val login: LoginUseCase,
    val logout: LogoutUseCase,
    val refreshToken: RefreshTokenUseCase,
    val isLoggedIn: IsLoggedInUseCase,
    val getCurrentUser: GetCurrentUserUseCase
)

/**
 * Use case for user login
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : UseCase<LoginUseCase.Params, User>(dispatcher) {

    data class Params(val username: String, val password: String)

    override suspend fun execute(parameters: Params): User {
        val result = authRepository.login(parameters.username, parameters.password)
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Login operation is still loading")
        }
    }
}

/**
 * Use case for user logout
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CompletableUseCase<Unit>(dispatcher) {

    override suspend fun execute(parameters: Unit) {
        val result = authRepository.logout()
        if (result is Result.Error) {
            throw result.exception
        }
    }
}

/**
 * Use case for refreshing authentication token
 */
class RefreshTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoParamUseCase<String>(dispatcher) {

    override suspend fun execute(): String {
        val result = authRepository.refreshToken()
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> throw IllegalStateException("Refresh token operation is still loading")
        }
    }
}

/**
 * Use case to check if user is logged in
 */
class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoParamUseCase<Boolean>(dispatcher) {

    override suspend fun execute(): Boolean {
        return authRepository.isLoggedIn()
    }
}

/**
 * Use case to get current user
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoParamUseCase<User?>(dispatcher) {

    override suspend fun execute(): User? {
        return authRepository.getCurrentUser()
    }
}