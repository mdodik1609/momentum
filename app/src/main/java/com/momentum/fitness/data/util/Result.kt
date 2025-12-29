package com.momentum.fitness.data.util

import kotlin.UnsafeVariance

/**
 * Sealed result class for better error handling
 * Replaces basic try-catch with type-safe result handling
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Throwable, String?) -> R,
        onLoading: () -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Error -> onError(exception, message)
        is Loading -> onLoading()
    }

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }

    inline fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(data)
        is Error -> this
        is Loading -> Loading
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrElse(default: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> default
    }
}

fun <T> Result<T>.getOrThrow(): T = when (this) {
    is Result.Success -> data
    is Result.Error -> throw exception
    is Result.Loading -> throw IllegalStateException("Result is still loading")
}


