package com.rpgportugal.orthanc.kt.persistence.sql.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.module.ModuleStateManagementConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.module.ModuleStateManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil.getSingleIdValue
import jakarta.persistence.EntityManager

class SqlModuleStateManagementConfigurationRepository(
    private val entityManager: EntityManager,
) : ModuleStateManagementConfigurationRepository {
    override fun getApplicationManagementConfiguration(): Either<DbError, ModuleStateManagementConfiguration> {
        val query =
            "select MSMC from ModuleStateManagementConfiguration MSMC " +
                    "where MSMC.id = 1"

        return entityManager
            .createQuery(query, ModuleStateManagementConfiguration::class.java)
            .getSingleIdValue(1L)

    }


}