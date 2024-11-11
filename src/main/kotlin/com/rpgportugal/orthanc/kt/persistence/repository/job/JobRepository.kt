package com.rpgportugal.orthanc.kt.persistence.repository.job

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration

interface JobRepository {
    fun getRoleAwardConfigurationByJobName(jobName: String): Either<DbError, RoleAwardConfiguration>
}