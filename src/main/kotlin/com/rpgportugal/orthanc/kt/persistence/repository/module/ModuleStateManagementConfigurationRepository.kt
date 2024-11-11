package com.rpgportugal.orthanc.kt.persistence.repository.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.module.ModuleStateManagementConfiguration

interface ModuleStateManagementConfigurationRepository {
    fun getApplicationManagementConfiguration(): Either<DbError, ModuleStateManagementConfiguration>
}