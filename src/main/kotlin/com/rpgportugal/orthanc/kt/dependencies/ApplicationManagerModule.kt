package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.discord.application.manager.impl.ApplicationManagerImpl
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object ApplicationManagerModule : DepModule {
    override val module: Module = module {
        singleOf(::getModules).bind(ApplicationManager::class)
    }

    private fun getModules(): ApplicationManagerImpl {
        return getKoin()
            .getAll<BotModule>()
            .associateBy { it.getName() }
            .let { modules -> ApplicationManagerImpl(modules) }
    }
}