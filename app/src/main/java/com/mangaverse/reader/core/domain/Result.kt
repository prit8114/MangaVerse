package com.mangaverse.reader.core.domain

/**
 * A generic class that holds a value or an error status.
 * @param T The type of the value that is wrapped in this result
 */
sealed class Result<out T> {
    /**
     * Represents successful operations with data
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Represents failed operations with error information
     */
    data class Error(val exception: Throwable) : Result<Nothing>()

    /**
     * Represents operations in progress
     */
    object Loading : Result<Nothing>()

    /**
     * Returns whether this result represents a successful operation
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Returns whether this result represents a failed operation
     */
    val isError: Boolean get() = this is Error

    /**
     * Returns whether this result represents an operation in progress
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Returns the encapsulated data if this instance represents [Success] or null otherwise
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    /**
     * Returns the encapsulated exception if this instance represents [Error] or null otherwise
     */
    fun exceptionOrNull(): Throwable? = when (this) {
        is Error -> exception
        else -> null
    }

    /**
     * Returns the encapsulated data if this instance represents [Success] or throws the encapsulated exception if it is [Error]
     */
    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }

    companion object {
        /**
         * Creates a [Result] instance from a catching block
         */
        inline fun <T> runCatching(block: () -> T): Result<T> = try {
            Success(block())
        } catch (e: Throwable) {
            Error(e)
        }
    }
}

/**
 * Maps the success value of this result using the given [transform] function
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
    is Result.Loading -> this
}

/**
 * Returns the result of [onSuccess] for the encapsulated value if this instance represents [Result.Success]
 * or the result of [onError] for the encapsulated [Throwable] if it is [Result.Error]
 * or the result of [onLoading] if it is [Result.Loading]
 */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Throwable) -> R,
    onLoading: () -> R
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(exception)
    is Result.Loading -> onLoading()
}