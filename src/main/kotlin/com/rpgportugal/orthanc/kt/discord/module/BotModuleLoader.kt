package com.rpgportugal.orthanc.kt.discord.module

import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import net.dv8tion.jda.api.JDA
import org.koin.core.component.KoinComponent

class BotModuleLoader : KoinComponent, Logging {

    val botModules: List<BotModule> = getKoin().getAll()

    fun loadModules( jda: JDA) {
        botModules.forEach { botModule ->
            val name = botModule.getName()
            log.info("===== Loading Module $name")
            if(!botModule.isEnabled()) {
                log.info("Module $name is disabled")
                return@forEach
            }

            botModule.attach(jda)
            log.info("Module $name loaded")
        }
    }
}