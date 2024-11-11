package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.BotModuleError
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.SpamCatcherConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class SpamCatcherModule(
    private val scheduler: Scheduler,
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
    private val jda: JDA
) : BotModule, Loggable {

    /*
        private val schedulerGroupName = "SpamCatcher"
        private val triggerName = "sendWarnMessageTrigger"
        private val jobName = "sendWarnMessageJob"

        private var linkRegex: String? = null
        private var honeypotChannelId: String? = null
        private var warningChannelId: String? = null
        private var cron: String = "0 *///5 * * * ? *"

    override fun getName(): String = "spam-catcher"

    override fun start(): Either<DomainError, TryCloseable> {

        val configuration = when(val res = botModuleConfigurationRepository.getSpamCatcherConfiguration()) {
            is Either.Right-> res.value
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