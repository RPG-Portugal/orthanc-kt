package com.rpgportugal.orthanc.kt.dependencies.bot

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.permission.PermissionManager
import com.rpgportugal.orthanc.kt.discord.permission.impl.PermissionManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object PermissionModule : DepModule {
    override val module: Module = module {
        singleOf(::PermissionManagerImpl).bind(PermissionManager::class)
    }
}