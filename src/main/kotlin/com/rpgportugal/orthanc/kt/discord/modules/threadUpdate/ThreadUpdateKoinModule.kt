package com.rpgportugal.orthanc.kt.discord.modules.threadUpdate

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import org.koin.dsl.module


object ThreadUpdateKoinModule : DepModule {
    override val module = module {
        single { ThreadUpdateModule(get()) }
    }
}