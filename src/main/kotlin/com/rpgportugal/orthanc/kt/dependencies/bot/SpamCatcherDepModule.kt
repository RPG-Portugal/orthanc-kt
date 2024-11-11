package com.rpgportugal.orthanc.kt.dependencies.bot

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.spam.catcher.SpamCatcherModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object SpamCatcherDepModule : DepModule {
    override val module = module {
        singleOf(::SpamCatcherModule).bind(BotModule::class)
    }
}