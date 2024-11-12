package com.rpgportugal.orthanc.kt.dependencies.bot

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object DiceModuleDepModule : DepModule {
    override val module: Module = module {
        singleOf(::DiceModule).bind(BotModule::class)
    }
}