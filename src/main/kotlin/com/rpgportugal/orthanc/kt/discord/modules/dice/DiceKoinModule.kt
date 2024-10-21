package com.rpgportugal.orthanc.kt.discord.modules.dice

import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.configuration.ResourcePropertiesLoader
import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


object DiceKoinModule : DepModule {
    override val module = module {
        single { DiceModule() } bind BotModule::class
    }
}