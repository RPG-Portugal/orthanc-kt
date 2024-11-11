package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SqlApplicationRepository(private val entityManager: EntityManager) : ApplicationRepository {

    companion object {
        @JvmStatic
        val LOG: Logger = LoggerFactory.getLogger(SqlApplicationRepository::class.java)
    }

    override fun getApplicationById(id: Long): Either<DbError, Application> {
        return try {
            val query = "select app from Application app where id = :id"

            val singleResult =
                entityManager
                    .createQuery(query, Application::class.java)
                    .setParameter("id", id).singleResult

            Either.Right(singleResult)
        } catch (e: NoResultException) {
            LOG.error("Failed to get application [no result found]", e)
            val name = Application::class.java.name
            return Either.Left(DbError.EntityNotFoundError(name, id, "Failed to find application $id"))
        } catch (e: NonUniqueResultException) {
            val name = Application::class.java.name
            LOG.error("Failed to get application [non unique result]", e)
            return Either.Left(DbError.EntityNotUnique(name, id, "Id not unique"))
        } catch (e: Exception) {
            LOG.error("Error getting application from database", e)
            Either.Left(ThrowableError(e))
        }
    }


}