package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.hooks.ListenerAdapter

class SpamCatcherModule(
    propertiesLoader: PropertiesLoader,
    private val scheduler: Scheduler,
) : ListenerAdapter(), BotModule {
/*
    companion object : DepModule {
        override val module = module {
            single { SpamCatcherModule(get(), get()) } bind BotModule::class
        }
    }

    private val schedulerGroupName = "SpamCatcher"
    private val triggerName = "sendWarnMessageTrigger"
    private val jobName = "sendWarnMessageJob"

    private var linkRegex: String? = null
    private var honeypotChannelId: String? = null
    private var warningChannelId: String? = null
    private var cron: String = "0 *///5 * * * ? *"

    /*
    private var regex: Regex? = null

    override fun getName(): String = "Spam Catcher"
    override fun start(): Either<DomainError, TryCloseable> {
        TODO("Not yet implemented")
    }

    fun attach(jda: JDA) {
        jda.addEventListener(this)
//       when (propertiesEither) {
//           is Either.Left -> {}
//            is Either.Right -> {
                linkRegex = TODO() //propertiesEither.value.getProperty("linkRegex")
                honeypotChannelId = TODO()//propertiesEither.value.getProperty("honeypotChannelId")
                warningChannelId = TODO()//propertiesEither.value.getProperty("warningChannelId")
                cron = TODO()//propertiesEither.value.getProperty("cron") ?:
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
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun start(): Either<DomainError, TryCloseable> {
        TODO("Not yet implemented")
    }
}