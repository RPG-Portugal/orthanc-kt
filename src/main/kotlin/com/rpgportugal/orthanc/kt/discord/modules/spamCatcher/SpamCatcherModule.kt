package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable

class SpamCatcherModule(
    private val scheduler: Scheduler,
) : BotModule, Loggable {

    /*
        private val schedulerGroupName = "SpamCatcher"
        private val triggerName = "sendWarnMessageTrigger"
        private val jobName = "sendWarnMessageJob"

        private var linkRegex: String? = null
        private var honeypotChannelId: String? = null
        private var warningChannelId: String? = null
        private var cron: String = "0 *///5 * * * ? *"

    /*

    private var regex: Regex? = null


    override fun start(): Either<DomainError, TryCloseable> {
        TODO("Not yet implemented")
    }

    fun attach(jda: JDA) {
//       when (propertiesEither) {
//           is Either.Left -> {}
//            is Either.Right -> {
                linkRegex =
                honeypotChannelId =
                warningChannelId =
                cron = :
                regex = if (linkRegex?.isNotBlank() == true) {
                    linkRegex!!.toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
                } else {
                    null
                }
                scheduler.simpleCronJobSchedule(
                    jobName,
                    triggerName,
                    schedulerGroupName,
                    cron,
                    HoneypotWarnJob::class.java,
                    JobDataMap().also {
                        it.put("jda", jda)
                        it.put("honeypotChannelId", honeypotChannelId)
                    })
            }
        }

    fun detach(jda: JDA) {
        //jda.removeEventListener(this)
    }

    fun onMessageReceived(event: MessageReceivedEvent) {
        val author = event.author
        val message = event.message

        //if (honeypotChannelId?.isBlank() != false) return
        if (author.isBot) return

        if (message.channel.id == honeypotChannelId) {
            message.delete().queue()

            if (regex?.matches(message.contentRaw) == true) {
                if (warningChannelId?.isNotEmpty() == true) {
                    val warningChannel = event.jda.getTextChannelById(warningChannelId!!)
                    warningChannel?.sendMessage(":x: Bani @ ${author.effectiveName} por escrever o seguinte no canal do mal: ${message.contentRaw}")
                        ?.queue()
                }

                event.guild.ban(listOf(author), Duration.ofDays(1L)).queue {
                    event.guild.unban(author).queue()
                }
            }
        }

    }
*/

    override fun getName(): String = "spam-catcher"

    override fun start(): Either<DomainError, TryCloseable> {
        TODO("Not yet implemented")
    }
}