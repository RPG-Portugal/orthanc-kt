package com.rpgportugal.orthanc.kt

import arrow.core.Either
import com.rpgportugal.orthanc.kt.dependencies.Modules
import com.rpgportugal.orthanc.kt.error.EntityNotFoundError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import dev.minn.jda.ktx.jdabuilder.createJDA
import net.dv8tion.jda.api.requests.GatewayIntent
import org.koin.core.context.startKoin
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger

val LOG: Logger = getLogger("main")

fun main() {
    LOG.info("Starting dependency modules")

    val koin = startKoin { modules(Modules.modules) }.koin

    LOG.info("Retrieving application")

    val applicationRepository = koin.get<ApplicationRepository>()

    val application: Application =
        when(val result = applicationRepository.getApplicationById(1L)) {
            is Either.Right -> result.value
            is Either.Left -> {
                val error = result.value
                LOG.error("Failed to retrieve application: $error")
                when (error) {
                    is EntityNotFoundError<*> ->
                        throw Exception("Entity ${error.entityName} with id = ${error.id} not found")
                    is ThrowableError<*> ->
                        throw error.exception
                }
            }
        }

    val jda = createJDA(
        token = application.token,
        enableCoroutines = true,
        intents = GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
}