package com.rpgportugal.orthanc.kt

import arrow.core.Either
import com.rpgportugal.orthanc.kt.dependencies.Modules
import com.rpgportugal.orthanc.kt.persistence.dto.Application
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import dev.minn.jda.ktx.jdabuilder.createJDA
import dev.minn.jda.ktx.jdabuilder.light
import net.dv8tion.jda.api.requests.GatewayIntent
import org.koin.core.context.startKoin
import org.slf4j.LoggerFactory.getLogger

fun main() {
    val log = getLogger("main")
    log.info("Starting...")

    val koin = startKoin { modules(Modules.modules) }.koin

    val applicationRepository = koin.get<ApplicationRepository>()
    
    val application: Application =
        when(val result = applicationRepository.getApplicationById(1L)) {
            is Either.Right -> result.value
            is Either.Left -> throw Exception(result.value.message)
        }

    val jda = createJDA(
        token = application.token,
        enableCoroutines = true,
        intents = GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
}