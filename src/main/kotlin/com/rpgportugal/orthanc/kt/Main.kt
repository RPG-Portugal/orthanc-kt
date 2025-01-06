package com.rpgportugal.orthanc.kt

import com.rpgportugal.orthanc.kt.dependencies.Modules
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import org.koin.core.context.startKoin

class Main {
    companion object : Loggable {
        @JvmStatic
        fun main(args: Array<String>) {
            val version =
                javaClass.getPackage()
                    .implementationVersion
                    ?: "Dev"

            log.info("Running version $version")

            log.info("main - starting dependency modules")
            val koin = startKoin { modules(Modules.modules) }.koin

            log.info("main - starting application")
            val appManager = koin.get<ModuleStateManager>()
            log.info("main - running application: {}", appManager)
        }
    }
}