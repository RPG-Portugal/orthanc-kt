package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceKoinModule
import com.rpgportugal.orthanc.kt.discord.modules.roleAward.RoleAwardKoinModule
import com.rpgportugal.orthanc.kt.discord.modules.threadUpdate.ThreadUpdateKoinModule


object Modules {
    val modules = listOf(
        PropertiesModule.module,
        DbModule.module,
        DiceKoinModule.module,
        ThreadUpdateKoinModule.module,
        RoleAwardKoinModule.module,
    )
}