package com.mangaverse.reader.core.domain

import com.mangaverse.reader.core.domain.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Base abstract class for use cases that return a single result
 */
abstract class UseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns a [Result].
     *
     * @param parameters the input parameters to run the use case with
     * @return a [Result] with success or error
     */
    suspend operator fun invoke(parameters: P): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters).let {
                    Result.Success(it)
                }
            }
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P): R
}

/**
 * Base abstract class for use cases that return a Flow of results
 */
abstract class FlowUseCase<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns a [Flow].
     *
     * @param parameters the input parameters to run the use case with
     * @return a [Flow] of [Result] with success or error
     */
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .map { Result.Success(it) as Result<R> }
            .catch { e -> kotlinx.coroutines.flow.flow { emit(Result.Error(e)) } }
            .flowOn(coroutineDispatcher)
    }

    /**
     * Override this to set the code to be executed.
     */
    protected abstract fun execute(parameters: P): Flow<R>
}

/**
 * Base abstract class for use cases that don't return anything (fire and forget)
 */
abstract class CompletableUseCase<in P>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns nothing.
     *
     * @param parameters the input parameters to run the use case with
     */
    suspend operator fun invoke(parameters: P): Result<Unit> {
        return try {
            withContext(coroutineDispatcher) {
                execute(parameters)
                Result.Success(Unit)
            }
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameters: P)
}

/**
 * A special case of [UseCase] where there are no input parameters
 */
abstract class NoParamUseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns a [Result].
     *
     * @return a [Result] with success or error
     */
    suspend operator fun invoke(): Result<R> {
        return try {
            withContext(coroutineDispatcher) {
                execute().let {
                    Result.Success(it)
                }
            }
        } catch (e: Throwable) {
            Result.Error(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): R
}

/**
 * A special case of [FlowUseCase] where there are no input parameters
 */
abstract class NoParamFlowUseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {

    /**
     * Executes the use case asynchronously and returns a [Flow].
     *
     * @return a [Flow] of [Result] with success or error
     */
    operator fun invoke(): Flow<Result<R>> {
        return execute()
            .map { Result.Success(it) as Result<R> }
            .catch { e -> flow { emit(Result.Error(e)) } }
            .flowOn(coroutineDispatcher)
    }

    /**
     * Override this to set the code to be executed.
     */
    protected abstract fun execute(): Flow<R>
}