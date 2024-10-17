package com.rpgportugal.orthanc.kt

import dev.minn.jda.ktx.jdabuilder.light
import net.dv8tion.jda.api.requests.*
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.slf4j.LoggerFactory.*

fun main() {
    val log = getLogger("main")
    log.info("Starting...")

    val koin = koinApplication {

    }

    val injector = startKoin(koin)
    

    val jda = light(token="", enableCoroutines = true) {
        setEnabledIntents(GatewayIntent.GUILD_MESSAGES)
    }




}