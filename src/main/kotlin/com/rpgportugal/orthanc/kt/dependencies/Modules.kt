package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.dependencies.bot.DiceModuleDepModule
import com.rpgportugal.orthanc.kt.dependencies.bot.RoleAwardKoinModule
import com.rpgportugal.orthanc.kt.dependencies.bot.SpamCatcherKoinModule
import com.rpgportugal.orthanc.kt.dependencies.bot.ThreadUpdateKoinModule
import com.rpgportugal.orthanc.kt.discord.modules.spamCatcher.SpamCatcherModule
import com.rpgportugal.orthanc.kt.scheduling.OrthancScheduler


object Modules {
    val modules = listOf(
        PropertiesModule,
        DbModule,
        OrthancScheduler,
        DiceModuleDepModule,
        ThreadUpdateKoinModule,
        RoleAwardKoinModule,
        SpamCatcherKoinModule,
        JdaModule,
        ApplicationManagerModule
    ).map {
        it.module
    }
}