package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.dto.job.JobConfiguration
import org.ktorm.schema.Table

object JobConfigurations : Table<JobConfiguration>("job_configuration") {

}