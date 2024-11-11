package com.rpgportugal.orthanc.kt.dependencies.bot

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.threadUpdate.ThreadUpdateModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


object ThreadUpdateKoinModule : DepModule {
    override val module = module {
        singleOf(::ThreadUpdateModule).bind(BotModule::class)
    }
}