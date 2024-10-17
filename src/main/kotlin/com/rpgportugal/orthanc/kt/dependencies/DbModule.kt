package com.rpgportugal.orthanc.kt.dependencies

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.application.db.SqlApplicationRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.ktorm.database.Database

object DbModule : DepModule{
    override val module = module {
        factory {
            val dbProperties = when (val result = get<PropertiesLoader>().load("secret/database.properties")) {
                is Either.Right -> result.value
                is Either.Left -> throw Exception(result.value.message)
            }

            val url = dbProperties.getProperty("url") ?: throw Exception("Missing url")

            Database.connect(url=url)
        }
        factoryOf(::SqlApplicationRepository) bind ApplicationRepository::class
    }
}