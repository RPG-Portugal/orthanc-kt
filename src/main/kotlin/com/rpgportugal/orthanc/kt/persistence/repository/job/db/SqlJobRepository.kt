package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import org.ktorm.database.Database

class SqlJobRepository(private val database: Database) : JobRepository {

}