package com.rpgportugal.orthanc.kt.dependencies

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import dev.minn.jda.ktx.jdabuilder.createJDA
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.GatewayIntent
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object JdaModule : DepModule, Logging {
    override val module: Module = module {
        single {
            val propertiesLoader = get<PropertiesLoader>()

            val applicationProperties =
                when (val result = propertiesLoader.load("app.properties")) {
                    is Either.Right -> result.value
                    is Either.Left -> {
                        val error = result.value
                        log.error("Failed to load application.properties => {}", error.message)
                        when (error) {
                            is ThrowableError<*> -> throw error.exception
                            is PropertiesLoadError.NullInputStreamError ->
                                throw Exception("Failed to retrieve ${error.fileName}")
                        }
                    }
                }

            val appId =
                applicationProperties
                    .getProperty("app.id")
                    ?.toLong()
                    ?: throw Exception("Missing app.id property")

            log.info("Retrieved application ID: {}", appId)

            val applicationRepository = get<ApplicationRepository>()

            val application: Application =
                when (val result = applicationRepository.getApplicationById(appId)) {
                    is Either.Right -> result.value
                    is Either.Left -> {
                        val error = result.value
                        log.error("Failed to retrieve application: $error")
                        when (error) {
                            is DbError.EntityNotFoundError<*> ->
                                throw Exception("Entity ${error.entityName} with id = ${error.id} not found")

                            is DbError.EntityNotUnique<*> ->
                                throw Exception("Entity ${error.entityName} with id = ${error.id} not unique")

                            is ThrowableError<*> ->
                                throw error.exception
                        }
                    }
                }

            createJDA(
                token = application.token,
                enableCoroutines = true,
                intents = GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
            )
        } bind JDA::class
    }

}