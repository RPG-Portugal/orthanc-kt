package com.rpgportugal.orthanc.kt.discord.modules.role.award

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.scheduling.Scheduler
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import org.quartz.JobDataMap
import java.time.OffsetDateTime

class RoleAwardListenerAdapter(
    private val jda: JDA,
    private val configuration: RoleAwardConfiguration,
    scheduler: Scheduler,
) : CloseableListenerAdapter(), Loggable {

    private val unscheduleJob: TryCloseable

    init {
        val jobConfiguration = configuration.jobConfiguration

        val result = scheduler.simpleCronJobSchedule(
            jobConfiguration.name,
            jobConfiguration.triggerName,
            jobConfiguration.schedulerGroupName,
            jobConfiguration.cronExpression,
            RoleCleanupJob::class.java,
            JobDataMap().also {
                it["jda"] = jda
                it["roleId"] = configuration.roleId
                it["adminAwardRole"] = configuration.adminAwardRoleId
                it["warningChannelId"] = configuration.warningChannelId
            })

        unscheduleJob = when (result) {
            is Either.Left -> {
                log.error("RoleAwardListenerAdapter - Failed to schedule job: {}", result.value.message)
                throw Exception(result.value.message)
            }

            is Either.Right -> {
                jda.addEventListener(this)
                result.value
            }
        }
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return unscheduleJob.tryClose()
        } catch (e: Exception) {
            log.error("tryClose - failed to close listener adapter", e)
            return ThrowableError(e)
        }
    }

    override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
        if (!configuration.emoji.let {
                if (event.emoji.type == Emoji.Type.UNICODE) {
                    event.emoji.asUnicode().asCodepoints == it.name
                } else {
                    event.emoji.name.lowercase() == it.name.lowercase()
                }
            }) return // Not the emoji we're looking for

        event.retrieveMessage().queue { message ->

            val startOfDay = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0)
            if (message.timeCreated.isBefore(startOfDay)) return@queue

            val count = message.getReaction(event.emoji)?.count ?: 0
            if (count < configuration.threshold) return@queue //Not enough emojis

            val author = event.jda.getUserById(event.messageAuthorIdLong)
                ?: return@queue //User not found (probably left the server)
            val member = event.guild.getMemberById(author.idLong)

            if (member == null) {
                log.error("RoleAwardListenerAdapter - No such member {} - ({})", author.name, author.effectiveAvatarUrl)
                return@queue
            }

            val roleId =
                if (member.hasPermission(Permission.ADMINISTRATOR))
                    configuration.adminAwardRoleId
                else
                    configuration.roleId


            val role = getRoleFromEventGuild(event, roleId) ?: run {
                log.error("Role {} not found", roleId)
                return@queue
            }

            if(member.roles.contains(role)) {
                log.info("RoleAwardListenerAdapter - Member already has role {}", role)
                return@queue
            }

            event.guild.addRoleToMember(member, role).queue()

            val warningChannel = event.jda.getTextChannelById(configuration.warningChannelId)
            warningChannel?.sendMessage(":lemon: Utilizador ${author.effectiveName} (@${author.name}) foi limonado (${message.jumpUrl}).")
                ?.queue()
        }
    }

    private fun getRoleFromEventGuild(event: MessageReactionAddEvent, roleId: Long): Role? =
        event.guild.getRoleById(roleId)

}