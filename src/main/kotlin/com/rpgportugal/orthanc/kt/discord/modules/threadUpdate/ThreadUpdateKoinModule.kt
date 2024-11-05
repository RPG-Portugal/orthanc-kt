package com.rpgportugal.orthanc.kt.discord.modules.threadUpdate

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.dsl.bind
import org.koin.dsl.module


object ThreadUpdateKoinModule : DepModule {
    override val module = module {
        single { ThreadUpdateModule() } bind BotModule::class
    }
}