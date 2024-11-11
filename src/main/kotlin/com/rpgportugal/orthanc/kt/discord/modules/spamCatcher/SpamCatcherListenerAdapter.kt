package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.SpamCatcherConfiguration
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.quartz.JobDataMap
import java.time.Duration

class SpamCatcherListenerAdapter(
    private val jda: JDA,
    private val configuration: SpamCatcherConfiguration,
    scheduler: Scheduler,
) : CloseableListenerAdapter(), Loggable {

    private val regex =
        configuration.linkRegex.toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))


    private val tryCloseJob: TryCloseable

    init {
        jda.addEventListener(this)

        val jobConfiguration = configuration.jobConfiguration

        val result = scheduler.simpleCronJobSchedule(
            jobConfiguration.name,
            jobConfiguration.triggerName,
            jobConfiguration.schedulerGroupName,
            jobConfiguration.cronExpression,
            HoneypotWarnJob::class.java,
            JobDataMap().also {
                it["jda"] = jda
                it["honeypotChannelId"] = configuration.honeypotChannelId
            })

        tryCloseJob = when (result) {
            is Either.Right -> result.value
            is Either.Left -> {
                log.error("init - {}", result.value.message)
                throw Exception(result.value.message)
            }
        }
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return tryCloseJob.tryClose()
        } catch (e: Exception) {
            return ThrowableError(e)
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val author = event.author
        val message = event.message

        //if (honeypotChannelId?.isBlank() != false) return
        if (author.isBot) return

        if (message.channel.idLong == configuration.honeypotChannelId) {
            message.delete().queue()

            if (regex.matches(message.contentRaw)) {
                val warningChannel = event.jda.getTextChannelById(configuration.warningChannelId)
                warningChannel?.sendMessage(":x: Bani @ ${author.effectiveName} por escrever o seguinte no canal do mal: ${message.contentRaw}")
                    ?.queue()

                event.guild.ban(listOf(author), Duration.ofDays(1L)).queue {
                    event.guild.unban(author).queue()
                }
            }
        }

    }
}