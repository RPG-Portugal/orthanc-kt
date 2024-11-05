package com.rpgportugal.orthanc.kt.dependencies

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError.NullInputStreamError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.application.db.SqlApplicationRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.ktorm.database.Database

object DbModule : DepModule {
    override val module = module {
        factory {
            val propertiesLoader = get<PropertiesLoader>()

            val dbProperties = when (val result = propertiesLoader.load("dev/secret/database.properties")) {
                is Either.Right -> result.value
                is Either.Left -> when (val error = result.value) {
                    is ThrowableError<*> -> throw error.exception
                    is NullInputStreamError -> throw Exception("${error.fileName} not found - ${error.message}")
                    is PropertiesLoadError.MissingPropertyError -> throw Exception("property ${error.propertyName} not found")
                }
            }

            val url =
                dbProperties.getProperty("url") ?: throw Exception("Missing url property from database.properties")

            Database.connect(url = url)
        }
        factory { SqlApplicationRepository(get()) } bind ApplicationRepository::class
    }
}