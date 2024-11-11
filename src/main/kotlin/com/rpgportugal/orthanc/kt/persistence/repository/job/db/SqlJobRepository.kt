package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import jakarta.persistence.EntityManager

class SqlJobRepository(private val entityManager: EntityManager) : JobRepository {

    fun getSql(jobName: String): List<RoleAwardConfiguration> {
        TODO()
    }
}