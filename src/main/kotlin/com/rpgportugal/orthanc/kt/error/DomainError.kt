package com.rpgportugal.orthanc.kt.error

sealed interface DomainError {
    val message: String
}

data class ThrowableError<E: Throwable>(
    val exception: E,
    override val message: String = exception.message ?: "No Message"
) : PropertiesLoadError, DatabaseError

// Properties Load Errors
sealed interface PropertiesLoadError : DomainError

data class NullInputStreamError(
    val fileName: String,
    override val message: String,
) : PropertiesLoadError

// Database Errors
sealed interface DatabaseError : DomainError

data class EntityNotFoundError<Id>(
    val entityName: String,
    val id: Id,
    override val message: String
) : DatabaseError
