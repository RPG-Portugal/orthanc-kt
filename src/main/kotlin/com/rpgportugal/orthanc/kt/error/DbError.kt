package com.rpgportugal.orthanc.kt.error

sealed interface DbError : DomainError {
    data class EntityNotFoundError<Id>(
        val entityName: String,
        val id: Id,
        override val message: String,
    ) : DbError

    class EntityNotUnique<Id>(
        val entityName: String,
        val id: Id,
        override val message: String,
    ) : DbError
}