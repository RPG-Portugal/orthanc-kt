package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import jakarta.persistence.EntityManager

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlApplicationRepository(private val entityManager: EntityManager) : ApplicationRepository {

    companion object {
        @JvmStatic
        val LOG: Logger = LoggerFactory.getLogger(SqlApplicationRepository::class.java)
    }

    override fun getApplicationById(id: Long): Either<DbError, Application> {
        return try {
            TODO()
        } catch (e: Exception) {
            LOG.error("Error getting application from database", e)
            Either.Left(ThrowableError(e))
        }
    }


}