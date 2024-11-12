package com.rpgportugal.orthanc.kt.discord.modules.management

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.permission.PermissionManager
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.ModuleStateManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toRight
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class ModuleStateManagement(
    private val jda: JDA,
    private val permissionManager: PermissionManager,
    private val moduleStateManagementConfigurationRepository: ModuleStateManagementConfigurationRepository,
) : BotModule {

    override fun getName(): String = "module-management"
    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {

        val configuration =
            when (val res = moduleStateManagementConfigurationRepository.getApplicationManagementConfiguration()) {
                is Either.Right -> res.value
                is Either.Left -> {
                    log.error("start - failed to get applicationManagementConfiguration - {}", res.value.message)
                    throw Exception(res.value.message)
                }
            }

        val listenerAdapter = ModuleStateManagementListenerAdapter(
            jda,
            moduleStateManager,
            permissionManager,
            configuration,
        )

        return TryCloseable { listenerAdapter.tryClose() }.toRight()
    }

}