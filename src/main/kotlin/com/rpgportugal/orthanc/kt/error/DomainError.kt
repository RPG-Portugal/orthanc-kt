package com.rpgportugal.orthanc.kt.error

import arrow.core.Either

sealed interface DomainError {
    val message: String
    fun asResult(): Either.Left<DomainError> {
        return Either.Left(this)
    }
}

data class ThrowableError<E : Throwable>(
    val exception: E,
    override val message: String = exception.message ?: "No Message",
) : PropertiesLoadError,
    DbError,
    ListenerCloseError,
    SchedulerError,
    BotModuleError,
    ModuleStateError