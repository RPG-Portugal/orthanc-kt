package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.discord.application.manager.impl.ApplicationManagerImpl
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object ApplicationManagerModule : DepModule {
    override val module: Module = module {
        single {
            val botModules: Map<String, BotModule> =
                getKoin()
                    .getAll<BotModule>()
                    .associateBy { it.getName() }

            ApplicationManagerImpl(botModules)
        } bind ApplicationManager::class
    }
}