package com.rpgportugal.orthanc.kt.discord.modules.newuserlookout

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.NewUserLookoutConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageType
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class NewUserLookoutListenerAdapter (
    private val jda: JDA,
    private val configuration: NewUserLookoutConfiguration,
) : CloseableListenerAdapter(), Loggable {

    val userCooldownMap = mutableMapOf<Long, Long>()

    init {
        jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.channel.idLong != configuration.listenChannelId) return //Is on right channel?
        if (event.message.type != MessageType.DEFAULT) return
        if( event.message.member?.roles?.find { r -> r.idLong == configuration.newUserRoleId } == null ) return //Has right role?

        if (userCooldownMap.containsKey( event.author.idLong) &&
            (System.currentTimeMillis() - userCooldownMap[event.author.idLong]!!) < 5*60*1000 ) return

        val mention = jda.getRoleById(configuration.pingRoleId)?.asMention

        val warningChannel = event.jda.getTextChannelById(configuration.warningChannelId)
        warningChannel?.sendMessage(":green_apple: Novo utilizador ${event.message.member?.effectiveName} (@${event.message.author.name}) respondeu (${event.message.jumpUrl}) $mention.")
            ?.queue()

        userCooldownMap[event.author.idLong] = System.currentTimeMillis()
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