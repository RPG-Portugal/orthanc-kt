package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.quartz.JobDataMap

class RoleAwardModule() : ListenerAdapter(), BotModule, KoinComponent {

    private val schedulerGroupName = "RoleAward"
    private val triggerName = "triggerEveryDayAt5AM"
    private val jobName = "roleCleanup"

    override val propertiesLoader: PropertiesLoader by inject<PropertiesLoader>()
    val scheduler: Scheduler by inject()
    override val propertiesEither = propertiesLoader.load("env/roleAwardModule.properties")

    var roleId: String? = null
    var threshold: Int = 99
    var emojiNames: String? = null
    var warningChannelId: String? = null
    var cron: String = "0 0 5 * * ? *"

    override fun getName(): String = "Role Award"

    override fun attach(jda: JDA) {
        jda.addEventListener(this)
        when (propertiesEither) {
            is Either.Left -> {}
            is Either.Right -> {
                roleId = propertiesEither.value.getProperty("roleId")
                warningChannelId = propertiesEither.value.getProperty("warningChannelId")
                emojiNames = propertiesEither.value.getProperty("emojiNames")
                threshold = propertiesEither.value.getProperty("threshold")?.toInt() ?: 99
                cron = propertiesEither.value.getProperty("cron") ?: "0 0 5 * * ? *"

                scheduler.simpleCronJobSchedule(
                    jobName,
                    triggerName,
                    schedulerGroupName,
                    cron,
                    RoleCleanupJob::class.java,
                    JobDataMap().also {
                        it.put("jda", jda)
                        it.put("roleId", roleId)
                        it.put("warningChannelId", warningChannelId)
                    })
            }
        }
    }

    override fun detach(jda: JDA) {
        jda.removeEventListener(this)
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (emojiNames?.split("|")?.any {
            if(event.emoji.type == Emoji.Type.UNICODE) { event.emoji.asUnicode().asCodepoints == it } else { event.emoji.name.lowercase() == it.lowercase() }
        } == false) return // Not the emoji we're looking for
        val role = event.guild.getRoleById(roleId?:return) ?: return // No role configured

        event.retrieveMessage().queue { message ->

            val count = message.getReaction(event.emoji)?.count ?: 0
            if(count < threshold) return@queue //Not enough emojis

            val author = event.jda.getUserById(event.messageAuthorIdLong) ?: return@queue //User not found (probably left the server)
            event.guild.addRoleToMember(author, role).queue()
            val warningChannel = event.jda.getTextChannelById(warningChannelId?:"")
            warningChannel?.sendMessage(":lemon: Utilizador ${author.effectiveName} (@${author.name}) foi limonado.")?.queue()
        }
    }
}