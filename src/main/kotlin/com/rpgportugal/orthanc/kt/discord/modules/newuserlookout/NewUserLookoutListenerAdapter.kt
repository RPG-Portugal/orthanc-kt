package com.rpgportugal.orthanc.kt.discord.modules.newuserlookout

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.NewUserLookoutConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class NewUserLookoutListenerAdapter (
    private val jda: JDA,
    private val configuration: NewUserLookoutConfiguration,
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.channel.idLong != configuration.listenChannelId) return //Is on right channel?
        if( event.message.member?.roles?.find { r -> r.idLong == configuration.newUserRoleId } == null ) return //Has right role?

        val mention = jda.getRoleById(configuration.pingRoleId)?.asMention

        val warningChannel = event.jda.getTextChannelById(configuration.warningChannelId)
        warningChannel?.sendMessage(":green_apple: Novo utilizador ${event.message.member?.effectiveName} (@${event.message.author.name}) respondeu (${event.message.jumpUrl}) $mention.")
            ?.queue()

    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        } catch (e: Exception) {
            log.error("tryClose - failed to close listener adapter", e)
            return ThrowableError(e)
        }
    }
}