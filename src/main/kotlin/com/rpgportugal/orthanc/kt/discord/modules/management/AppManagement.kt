package com.rpgportugal.orthanc.kt.discord.modules.management

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.permission.PermissionManager
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.ApplicationManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toRight
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class AppManagement(
    val jda: JDA,
    val permissionManager: PermissionManager,
    val applicationManagementConfigurationRepository: ApplicationManagementConfigurationRepository
) : BotModule {
    override fun getName(): String = "app-management"

    override fun start(applicationManager: ApplicationManager): Either<DomainError, TryCloseable> {

        val configuration =
            when (val res = applicationManagementConfigurationRepository.getApplicationManagementConfiguration()){
                is Either.Right -> res.value
                is Either.Left -> {
                    log.error("start - failed to get applicationManagementConfiguration - {}", res.value.message)
                    throw Exception(res.value.message)
                }
            }

        val listenerAdapter = AppManagementListenerAdapter(
            jda,
            applicationManager,
            permissionManager,
            configuration,
        )

        return TryCloseable { listenerAdapter.tryClose() }.toRight()
    }

}