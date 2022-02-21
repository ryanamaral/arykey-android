package com.ryanamaral.arykey.common.domain


/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(val error: Throwable) : Result<Nothing>()

    object Loading : Result<Nothing>()

    fun isLoading() = this is Loading
    fun isSuccessful() = this is Success

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$error]"
            Loading -> "Loading"
        }
    }
}

fun <T> Result<T>?.successOr(fallback: T): T {
    if (this == null) return fallback
    return (this as? Result.Success<T>)?.data ?: fallback
}

val <T> Result<T>.data: T?
    get() = (this as? Result.Success)?.data
