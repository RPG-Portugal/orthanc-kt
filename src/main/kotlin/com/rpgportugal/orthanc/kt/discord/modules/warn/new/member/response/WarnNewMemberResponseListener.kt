package com.rpgportugal.orthanc.kt.discord.modules.warn.new.member.response

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import dev.minn.jda.ktx.events.awaitMessage
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

class WarnNewMemberResponseListener(
    private val jda: JDA,
    private val arrivalChannel: TextChannel,
    private val warnChannel : TextChannel,
    private val moderatorRole : Role,
) : CloseableListenerAdapter(), Loggable {


    init {
        jda.addEventListener(this)
    }

    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        runBlocking {
            val user = event.member.user
            val message = arrivalChannel.awaitMessage(user)
            log.info("onGuildMemberJoin - received first message for user ${user.name}")

            val jumpUrl = message.jumpUrl

            val warnMessage =
                "${moderatorRole.asMention} - novo utilizador ${user.asMention} respondeu pela primeira na sala $jumpUrl"

            log.info("onGuildMemberJoin - sending message to warn channel")
            warnChannel.sendMessage(warnMessage).queue { t ->
                log.info("onGuildMemberJoin - join message sent for {}", user.name)
            }
        }
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        }catch (e: Exception) {
            return ThrowableError(e)
        }
    }
}