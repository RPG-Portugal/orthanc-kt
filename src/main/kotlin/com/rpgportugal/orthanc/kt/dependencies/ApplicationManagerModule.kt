package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.application.manager.impl.ModuleStateManagerImpl
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

object ApplicationManagerModule : DepModule {
    override val module: Module = module {
        singleOf(::getModules).bind(ModuleStateManager::class)
    }

    private fun getModules(): ModuleStateManagerImpl {
        return getKoin()
            .getAll<BotModule>()
            .associateBy { it.getName() }
            .let { modules -> ModuleStateManagerImpl(modules) }
    }
}