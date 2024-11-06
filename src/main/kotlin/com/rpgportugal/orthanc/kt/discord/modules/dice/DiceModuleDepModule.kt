package com.rpgportugal.orthanc.kt.discord.modules.dice

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

object DiceModuleDepModule : DepModule {
    override val module: Module = module {
        single { DiceModule(get(), get()) } bind BotModule::class
    }
}