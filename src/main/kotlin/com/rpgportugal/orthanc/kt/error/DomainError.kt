package com.rpgportugal.orthanc.kt.error

interface DomainError {
    val message: String
}

data class ThrowableError<E: Throwable>(
    val exception: E,
    override val message: String = exception.message ?: "No Message"
) : PropertiesLoadError, DatabaseError

// Property Errors
// Properties Load Errors
sealed interface PropertiesLoadError : DomainError

data class NullInputStreamError(
    val fileName: String,
    override val message: String,
) : PropertiesLoadError

// Property Value Errors
sealed interface PropertyValueError : DomainError
data class MissingPropertyError(
    val propertyName: String,
    override val message: String,
) : PropertyValueError

// Database Errors
sealed interface DatabaseError : DomainError

data class EntityNotFoundError<Id>(
    val entityName: String,
    val id: Id,
    override val message: String
) : DatabaseError
