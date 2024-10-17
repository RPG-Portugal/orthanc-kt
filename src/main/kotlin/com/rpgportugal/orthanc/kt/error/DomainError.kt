package com.rpgportugal.orthanc.kt.error

sealed interface DomainError {
    val message: String
}

data class ThrowableError<E: Throwable>(
    val exception: E,
    override val message: String = exception.message ?: "No Message"
) : PropertiesLoadError

sealed interface PropertiesLoadError : DomainError

data class NullInputStream(
    val fileName: String,
    override val message: String,
) : PropertiesLoadError
