package com.rpgportugal.orthanc.kt.persistence.sql.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.module.ApplicationManagementConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.module.ApplicationManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil.getSingleIdValue
import jakarta.persistence.EntityManager

class SqlApplicationManagementConfigurationRepository(
    private val entityManager: EntityManager
) : ApplicationManagementConfigurationRepository {
    override fun getApplicationManagementConfiguration(): Either<DbError, ApplicationManagementConfiguration> {
        val query =
            "select AMC from ApplicationManagementConfiguration AMC " +
                    "where AMC.id = 1"

        return entityManager
            .createQuery(query, ApplicationManagementConfiguration::class.java)
            .getSingleIdValue(1L)

    }


}