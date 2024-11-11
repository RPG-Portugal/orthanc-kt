package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.dependencies.bot.DiceModuleDepModule
import com.rpgportugal.orthanc.kt.dependencies.bot.RoleAwardKoinModule
import com.rpgportugal.orthanc.kt.dependencies.bot.ThreadUpdateKoinModule
import com.rpgportugal.orthanc.kt.scheduling.OrthancScheduler


object Modules {
    val modules = listOf(
        PropertiesModule,
        DbModule,
        OrthancScheduler,
        DiceModuleDepModule,
        ThreadUpdateKoinModule,
        RoleAwardKoinModule,
        // SpamCatcherModule,
        JdaModule,
        ApplicationManagerModule
    ).map {
        it.module
    }
}