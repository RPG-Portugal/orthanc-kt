package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import org.koin.dsl.module


object RoleAwardKoinModule : DepModule {
    override val module = module {
        single { RoleAwardModule(get(), get()) }
    }
}