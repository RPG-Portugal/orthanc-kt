package com.rpgportugal.orthanc.kt.persistence.repository.application

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.application.Application


interface ApplicationRepository {
    fun getActiveApplication(): Either<DbError, Application>
}