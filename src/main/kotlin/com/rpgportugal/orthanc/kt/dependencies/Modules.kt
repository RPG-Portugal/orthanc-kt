package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.dependencies.bot.*
import com.rpgportugal.orthanc.kt.scheduling.OrthancScheduler


object Modules {
    val modules = listOf(
        PermissionModule,
        PropertiesModule,
        DbModule,
        OrthancScheduler,
        DiceModuleDepModule,
        ThreadUpdateDepModule,
        RoleAwardDepModule,
        SpamCatcherDepModule,
        ApplicationManagementDepModule,
        JdaModule,
        ApplicationManagerModule
    ).map {
        it.module
    }
}