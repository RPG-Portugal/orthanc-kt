package com.rpgportugal.orthanc.kt

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.dependencies.Modules
import com.rpgportugal.orthanc.kt.discord.module.BotModuleLoader
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import dev.minn.jda.ktx.jdabuilder.createJDA
import net.dv8tion.jda.api.requests.GatewayIntent
import org.koin.core.context.startKoin
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

val LOG: Logger = getLogger("main")

class Main {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            LOG.info("Starting dependency modules")

            val koin = startKoin { modules(Modules.modules) }.koin

            LOG.info("Retrieving application ID")

            val propertiesLoader = koin.get<PropertiesLoader>()

            val applicationProperties =
                when (val result = propertiesLoader.load("env/application.properties")) {
                    is Either.Right -> result.value
                    is Either.Left -> {
                        val error = result.value
                        LOG.error("Failed to load application.properties => {}", error.message)
                        when (error) {
                            is ThrowableError<*> -> throw error.exception
                            is PropertiesLoadError.NullInputStreamError ->
                                throw Exception("Failed to retrieve ${error.fileName}")
                            is PropertiesLoadError.MissingPropertyError ->
                                throw Exception("Failed to retrieve property ${error.propertyName}")
                        }
                    }
                }

            val appId =
                applicationProperties
                    .getProperty("app.id")
                    ?.toLong()
                    ?: throw Exception("Missing app.id property")

            LOG.info("Retrieved application ID: {}", appId)

            val applicationRepository = koin.get<ApplicationRepository>()

            val application: Application =
                when (val result = applicationRepository.getApplicationById(appId)) {
                    is Either.Right -> result.value
                    is Either.Left -> {
                        val error = result.value
                        LOG.error("Failed to retrieve application: $error")
                        when (error) {
                            is DbError.EntityNotFoundError<*> ->
                                throw Exception("Entity ${error.entityName} with id = ${error.id} not found")

                            is ThrowableError<*> ->
                                throw error.exception
                        }
                    }
                }

            val jda = createJDA(
                token = application.token,
                enableCoroutines = true,
                intents = GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
            )

            BotModuleLoader().loadModules(jda)
        }
    }
}