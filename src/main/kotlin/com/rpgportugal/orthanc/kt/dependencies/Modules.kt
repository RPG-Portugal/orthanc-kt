package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceModule
import org.koin.dsl.bind
import org.koin.dsl.module


object Modules {
    val modules = listOf(
        PropertiesModule.module,
        DbModule.module,
        module {
            single { DiceModule() } bind BotModule::class
        }
    )
}