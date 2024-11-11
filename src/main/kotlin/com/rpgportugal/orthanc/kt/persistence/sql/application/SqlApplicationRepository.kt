package com.rpgportugal.orthanc.kt.persistence.sql.application

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil.getSingleIdValue
import jakarta.persistence.EntityManager

class SqlApplicationRepository(private val entityManager: EntityManager) : ApplicationRepository, Loggable {

    override fun getActiveApplication(): Either<DbError, Application> {


        val query =
            "select AAPP.application from ActiveApplication AAPP " +
                    "join fetch Application APP on AAPP.id = APP.id " +
                    "where AAPP.active = true"




        return entityManager
            .createQuery(query, Application::class.java)
            .getSingleIdValue(true)
    }


}