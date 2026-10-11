package com.maroondevelopment.networth.data.model

/**
 * Outcome of a data-layer operation: either [Success] carrying a [T], or an [Error] describing
 * why the operation failed.
 *
 * [T] is covariant and [Error] is a `Result<Nothing>`, so a single error value fits wherever a
 * `Result<T>` is expected and callers never have to re-wrap it per call site.
 */
sealed interface Result<out T> {

    data class Success<out T>(val data: T) : Result<T>

    sealed interface Error : Result<Nothing> {

        /** No response at all — offline, timeout, DNS or TLS failure. */
        data class Network(val cause: Throwable? = null) : Error

        /** The server answered, but not successfully. */
        data class Http(val status: Int) : Error

        /** A response arrived but could not be read into the expected shape. */
        data class Serialization(val cause: Throwable? = null) : Error

        /** A local database read or write failed. */
        data class Storage(val cause: Throwable? = null) : Error

        /** The call worked, but the thing asked for does not exist. */
        data object NotFound : Error
    }
}

/** The value on success, or `null` on any [Result.Error]. */
fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

/** Transforms a [Result.Success] value, passing any [Result.Error] through untouched. */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}

/** Collapses both branches into a single value. */
inline fun <T, R> Result<T>.fold(
    onSuccess: (T) -> R,
    onError: (Result.Error) -> R
): R = when (this) {
    is Result.Success -> onSuccess(data)
    is Result.Error -> onError(this)
}
