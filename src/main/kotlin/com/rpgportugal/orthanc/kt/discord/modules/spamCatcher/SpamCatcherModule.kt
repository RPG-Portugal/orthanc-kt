package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.bind
import org.koin.dsl.module
import org.quartz.JobDataMap
import java.time.Duration

class SpamCatcherModule() : ListenerAdapter(), BotModule, KoinComponent {

    companion object : DepModule {
        override val module = module {
            single { SpamCatcherModule() } bind BotModule::class
        }
    }

    private val schedulerGroupName = "SpamCatcher"
    private val triggerName = "sendWarnMessageTrigger"
    private val jobName = "sendWarnMessageJob"

    override val propertiesLoader: PropertiesLoader by inject<PropertiesLoader>()
    val scheduler: Scheduler by inject()
    override val propertiesEither = propertiesLoader.load("env/spamCatcher.properties")

    var linkRegex: String? = null
    var honeypotChannelId: String? = null
    var warningChannelId: String? = null
    var cron: String = "0 */5 * * * ? *"
    var regex: Regex? = null

    override fun getName(): String = "Spam Catcher"

    override fun attach(jda: JDA) {
        jda.addEventListener(this)
        when (propertiesEither) {
            is Either.Left -> {}
            is Either.Right -> {
                linkRegex = propertiesEither.value.getProperty("linkRegex")
                honeypotChannelId = propertiesEither.value.getProperty("honeypotChannelId")
                warningChannelId = propertiesEither.value.getProperty("warningChannelId")
                cron = propertiesEither.value.getProperty("cron") ?: "0 */5 * * * ? *"
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
    }

    override fun detach(jda: JDA) {
        jda.removeEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val author = event.author
        val message = event.message

        if (honeypotChannelId?.isBlank() != false) return
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
}