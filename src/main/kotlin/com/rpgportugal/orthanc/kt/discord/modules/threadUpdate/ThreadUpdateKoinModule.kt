package com.rpgportugal.orthanc.kt.discord.modules.threadUpdate

import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.configuration.ResourcePropertiesLoader
import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceModule
import com.rpgportugal.orthanc.kt.discord.modules.roleAward.RoleAwardModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


object ThreadUpdateKoinModule : DepModule {
    override val module = module {
        single { ThreadUpdateModule() } bind BotModule::class
    }
}