package com.rpgportugal.orthanc.kt.persistence.repository.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.module.ApplicationManagementConfiguration

interface ApplicationManagementConfigurationRepository {
    fun getApplicationManagementConfiguration() : Either<DbError, ApplicationManagementConfiguration>
}