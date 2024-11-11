package com.rpgportugal.orthanc.kt.persistence.sql.util

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException
import jakarta.persistence.TypedQuery

object QueryUtil : Loggable {

    fun <T> getSingleIdValue(typedQuery: TypedQuery<T>, entityClass: Class<T>): Either<DbError, T> {
        return getSingleIdValue(typedQuery, entityClass, null)
    }

    fun <I, T> getSingleIdValue(typedQuery: TypedQuery<T>, entityClass: Class<T>, id: I?): Either<DbError, T> {
        try {
            return Either.Right(typedQuery.singleResult)
        } catch (e: NoResultException) {
            log.error("getSingleIdValue - Failed to find entity with id = {}", id, e)
            return Either.Left(
                DbError.EntityNotFoundError(
                    entityClass.name,
                    id,
                    e.message ?: "Failed to find entity with id $id"
                )
            )
        } catch (e: NonUniqueResultException) {
            log.error("getSingleIdValue - Failed to find entity with id = {}", id, e)
            return Either.Left(
                DbError.EntityNotFoundError(
                    entityClass.name,
                    id,
                    e.message ?: "entity with id = $id not unique"
                )
            )
        } catch (e: Exception) {
            log.error("getSingleIdValue - Failed to retrieve entity with id = {}", id, e)
            return Either.Left(ThrowableError(e))
        }
    }
}