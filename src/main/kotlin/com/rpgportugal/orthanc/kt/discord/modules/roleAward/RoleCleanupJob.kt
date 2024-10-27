package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import com.rpgportugal.orthanc.kt.logging.Logging
import net.dv8tion.jda.api.JDA
import org.quartz.Job
import org.quartz.JobExecutionContext

class RoleCleanupJob : Job, Logging {
    override fun execute(context: JobExecutionContext?) {
        val jobDataMap = context?.jobDetail?.jobDataMap
        val jda = (jobDataMap?.get("jda") ?: return) as JDA
        val roleId = jobDataMap.getString("roleId") ?: return
        val warningChannelId = jobDataMap.getString("warningChannelId") ?: return
        val warningChannel = jda.getTextChannelById(warningChannelId)

        jda.guilds.forEach { guild ->
            val role = guild.getRoleById(roleId) ?: return@forEach
            guild.getMembersWithRoles(role).forEach { member ->
                guild.removeRoleFromMember(member, role).queue()
                warningChannel?.sendMessage(":sparkles: Utilizador ${member.effectiveName} (@${member.user.name}) foi deslimonado.")?.queue()
            }
        }
    }
}