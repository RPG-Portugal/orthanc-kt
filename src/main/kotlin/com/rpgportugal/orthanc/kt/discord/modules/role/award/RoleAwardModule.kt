package com.rpgportugal.orthanc.kt.discord.modules.role.award

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class RoleAwardModule(
    private val scheduler: Scheduler,
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
    private val jda: JDA,
) : BotModule, Loggable {

    //    private val schedulerGroupName = "RoleAward"
    //    private val triggerName = "triggerEveryDayAt5AM"
    //    private val jobName = "roleCleanup"
    //    private var cron: String = "0 0 5 * * ? *"

    override fun getName(): String = "role-award"

    override fun start(applicationManager: ApplicationManager): Either<DomainError, TryCloseable> {

        val configuration = when (val result = botModuleConfigurationRepository.getRoleAwardConfiguration()) {
            is Either.Right -> result.value
            is Either.Left -> {
                log.error("Failed to get RoleAward configuration: {}", result.value.message)
                throw Exception(result.value.message)
            }
        }

        val listenerAdapter = RoleAwardListenerAdapter(jda, configuration, scheduler)

        return TryCloseable { listenerAdapter.tryClose() }.asResult()
    }
}

