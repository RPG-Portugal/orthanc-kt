package com.rpgportugal.orthanc.kt.persistence.sql.application

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlApplicationRepository(private val entityManager: EntityManager) : ApplicationRepository, Loggable {

    override fun getActiveApplication(): Either<DbError, Application> {

        val cls = Application::class.java

        val query =
            "select AAPP.application from ActiveApplication AAPP " +
                    "join fetch Application APP on AAPP.id = APP.id " +
                    "where AAPP.active = true"

        val typedQuery =
            entityManager
                .createQuery(query, cls)

        return QueryUtil.getSingleIdValue(typedQuery, cls, true)
    }


}