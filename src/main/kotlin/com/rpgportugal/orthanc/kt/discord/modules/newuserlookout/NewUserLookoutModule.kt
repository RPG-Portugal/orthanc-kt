package com.rpgportugal.orthanc.kt.discord.modules.newuserlookout

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.role.award.RoleAwardListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class NewUserLookoutModule(
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
    private val jda: JDA,
) : BotModule, Loggable {

    override fun getName(): String = "new-user-lookout-module"

    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {

        val configuration = when (val result = botModuleConfigurationRepository.getNewUserLookoutConfiguration()) {
            is Either.Right -> result.value
            is Either.Left -> {
                log.error("Failed to get NewUserLookout configuration: {}", result.value.message)
                throw Exception(result.value.message)
            }
        }

        val listenerAdapter = NewUserLookoutListenerAdapter(jda, configuration)

        return TryCloseable { listenerAdapter.tryClose() }.asResult()
    }
}

