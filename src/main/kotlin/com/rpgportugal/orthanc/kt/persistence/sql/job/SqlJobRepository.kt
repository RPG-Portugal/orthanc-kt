package com.rpgportugal.orthanc.kt.persistence.sql.job

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

class SqlJobRepository(private val entityManager: EntityManager) : JobRepository, Loggable