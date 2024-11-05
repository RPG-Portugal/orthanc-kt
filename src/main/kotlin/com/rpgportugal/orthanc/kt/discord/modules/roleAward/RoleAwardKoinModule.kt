package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import org.koin.dsl.bind
import org.koin.dsl.module


object RoleAwardKoinModule : DepModule {
    override val module = module {
        single { RoleAwardModule() } bind BotModule::class
    }
}