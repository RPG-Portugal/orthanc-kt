package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlApplicationRepository(private val database: Database) : ApplicationRepository {

    companion object {
        @JvmStatic
        val LOG: Logger = LoggerFactory.getLogger(SqlApplicationRepository::class.java)
    }

    override fun getApplicationById(id: Long): Either<DbError, Application> {
        return try {
            val application =
                database
                    .sequenceOf(Applications)
                    .firstOrNull { app -> app.id eq id }

            if (application != null) {
                Either.Right(application)
            } else {
                LOG.error("Failed to find application with id = {}", id)
                Either.Left(
                    DbError.EntityNotFoundError(
                        Applications.tableName,
                        id,
                        "Failed to find application with id = $id"
                    )
                )
            }
        } catch (e: Exception) {
            LOG.error("Error getting application from database", e)
            Either.Left(ThrowableError(e))
        }
    }


}