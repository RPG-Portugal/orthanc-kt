package com.rpgportugal.orthanc.kt.persistence.repository.application

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application


interface ApplicationRepository {
    fun getApplicationById(id: Long): Either<DbError, Application>
}