package com.rpgportugal.orthanc.kt

import com.rpgportugal.orthanc.kt.dependencies.Modules
import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import org.koin.core.context.startKoin

class Main {
    companion object : Logging {
        @JvmStatic
        fun main(args: Array<String>) {
            log.info("main - starting dependency modules")
            val koin = startKoin { modules(Modules.modules) }.koin

            log.info("main - starting application")
            koin.get<ApplicationManager>()
        }
    }
}