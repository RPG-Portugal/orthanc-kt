package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.filter
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList

class SqlJobRepository(private val database: Database) : JobRepository {

    fun getSql(jobName: String): List<RoleAwardConfiguration> {
        return database
            .sequenceOf(RoleAwardConfigurations, true)
            .filter { it.jobName eq jobName }
            .toList()
    }
}