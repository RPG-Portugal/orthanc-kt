package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DatabaseError
import com.rpgportugal.orthanc.kt.error.EntityNotFoundError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import org.koin.java.KoinJavaComponent.inject
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class SqlApplicationRepository : ApplicationRepository {
    private val database: Database by inject(Database::class.java)

    override fun getApplicationById(id: Long) : Either<DatabaseError,Application>{
        return try {
            val application =
                database
                    .sequenceOf(Applications)
                    .find { app -> app.id eq id  }

            if (application != null){
                Either.Right(application)
            } else {
                Either.Left(
                    EntityNotFoundError(
                        Applications.tableName,
                        id,
                        "Failed to find application"
                    )
                )
            }
        } catch (e: Exception) {
            Either.Left(ThrowableError(e))
        }
    }


}