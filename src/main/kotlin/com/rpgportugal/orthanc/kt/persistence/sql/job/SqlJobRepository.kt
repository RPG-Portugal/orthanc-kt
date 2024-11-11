package com.rpgportugal.orthanc.kt.persistence.sql.job

import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import jakarta.persistence.EntityManager

class SqlJobRepository(private val entityManager: EntityManager) : JobRepository, Loggable