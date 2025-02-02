package com.rpgportugal.orthanc.kt.discord.modules.spam.catcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.BotModuleError
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class SpamCatcherModule(
    private val scheduler: Scheduler,
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
    private val jda: JDA,
) : BotModule, Loggable {

    override fun getName(): String = "spam-catcher"

    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {

        val configuration = when (val res = botModuleConfigurationRepository.getSpamCatcherConfiguration()) {
            is Either.Right -> res.value
            is Either.Left -> {
                log.error("start - {}", res.value.message)
                return res
            }
        }

        if (configuration.linkRegex.isBlank()) {
            log.error("start - link regex cannot be blank")
            return BotModuleError.InvalidConfiguration("link regex is blank").asResult()
        }


        val listenerAdapter = SpamCatcherListenerAdapter(
            jda,
            configuration,
            scheduler
        )

        return TryCloseable { listenerAdapter.tryClose() }.asResult()
    }
}