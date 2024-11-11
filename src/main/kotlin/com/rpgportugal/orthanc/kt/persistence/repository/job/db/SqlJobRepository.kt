package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

class SqlJobRepository(private val entityManager: EntityManager) : JobRepository, Logging {

    override fun getRoleAwardConfigurationByJobName(jobName: String): Either<DbError, RoleAwardConfiguration> {
        try {
            val query =
                "select RA from RoleAwardConfiguration RA " +
                        "join fetch Emoji E " +
                        "join fetch JobConfiguration " +
                        "where RA.name = :name "

            val cls = RoleAwardConfiguration::class.java
            val result =
                entityManager.createQuery(query, cls)
                    .setParameter("name", jobName)
                    .singleResult

            return Either.Right(result)
        } catch (e: NoResultException) {
            log.error("Failed to find RoleAwardConfiguration for job $jobName", e)
            return Either.Left(
                DbError.EntityNotFoundError(
                    RoleAwardConfiguration::class.java.name,
                    jobName,
                    e.message ?: "Failed to find RoleAwardConfiguration for job $jobName"
                )
            )
        } catch (e: NonUniqueResultException) {
            return Either.Left(
                DbError.EntityNotFoundError(
                    RoleAwardConfiguration::class.java.name,
                    jobName,
                    e.message ?: "RoleAwardConfiguration for job $jobName not unique"
                )
            )
        } catch (e: Exception) {
            log.error("Failed to retrieve role award configuration", e)
            return Either.Left(ThrowableError(e))
        }
    }
}