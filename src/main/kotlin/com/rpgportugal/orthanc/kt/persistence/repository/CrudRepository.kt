package com.rpgportugal.orthanc.kt.persistence.repository

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError

interface CrudRepository<T, ID> {
    fun findById(id: ID): Either<DbError, T?>
    fun findAll(): Either<DbError, List<T>>
    fun save(entity: T): Either<DbError, T>
    fun delete(id: ID): Either<DbError, Unit>
}
