package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceModule
import com.rpgportugal.orthanc.kt.discord.modules.roleAward.RoleAwardKoinModule
import com.rpgportugal.orthanc.kt.discord.modules.spamCatcher.SpamCatcherModule
import com.rpgportugal.orthanc.kt.discord.modules.threadUpdate.ThreadUpdateKoinModule
import com.rpgportugal.orthanc.kt.scheduling.OrthancScheduler


object Modules {
    val modules = listOf(
        PropertiesModule.module,
        DbModule.module,
        OrthancScheduler.module,
        DiceModule.module,
        ThreadUpdateKoinModule.module,
        RoleAwardKoinModule.module,
        SpamCatcherModule.module,
    )
}