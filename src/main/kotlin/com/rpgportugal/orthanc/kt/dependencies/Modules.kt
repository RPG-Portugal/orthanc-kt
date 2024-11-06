package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceModuleDepModule
import com.rpgportugal.orthanc.kt.discord.modules.roleAward.RoleAwardKoinModule
import com.rpgportugal.orthanc.kt.discord.modules.threadUpdate.ThreadUpdateKoinModule
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