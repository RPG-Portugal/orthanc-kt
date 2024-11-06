package com.rpgportugal.orthanc.kt.dependencies

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError.NullInputStreamError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.application.db.SqlApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.db.SqlEmojiRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.ktorm.database.Database

object DbModule : DepModule {
    override val module = module {
        factory {
            val propertiesLoader = get<PropertiesLoader>()

            val dbProperties = when (val result = propertiesLoader.load("secret/database.properties")) {
                is Either.Right -> result.value
                is Either.Left -> {
                    log.error("Failed to load from app.properties.example - {}", result.value)
                    when (val error = result.value) {
                        is NullInputStreamError -> throw Exception("${error.fileName} not found - ${error.message}")
                        is ThrowableError<*> -> throw error.exception
                    }
                }
            }

            val url =
                dbProperties.getProperty("url") ?: throw Exception("Missing url property from database.properties")

            Database.connect(url = url)
        }
        factory { SqlApplicationRepository(get()) } bind ApplicationRepository::class
        factory { SqlEmojiRepository(get()) } bind EmojiRepository::class
    }
}