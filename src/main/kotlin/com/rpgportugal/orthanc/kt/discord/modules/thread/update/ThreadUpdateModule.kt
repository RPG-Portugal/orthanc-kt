package com.rpgportugal.orthanc.kt.discord.modules.thread.update

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ThreadUpdateModule(
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
    private val jda: JDA,
) : ListenerAdapter(), BotModule, Loggable {

    override fun getName(): String = "thread-update"
    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {
        val configuration = when (val res = botModuleConfigurationRepository.getThreadUpdateConfiguration()) {
            is Either.Right -> res.value
            is Either.Left -> {
                log.error("start - {}", res.value.message)
                return res
            }
        }

        val adapter = ThreadUpdateListenerAdapter(jda, configuration)

        return TryCloseable { adapter.tryClose() }.asResult()
    }
}