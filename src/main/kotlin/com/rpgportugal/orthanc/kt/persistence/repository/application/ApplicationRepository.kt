package com.rpgportugal.orthanc.kt.persistence.repository.application

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DatabaseError
import com.rpgportugal.orthanc.kt.persistence.dto.Application


interface ApplicationRepository {
    fun getApplicationById(id: Long): Either<DatabaseError, Application>
}