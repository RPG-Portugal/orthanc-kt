package com.rpgportugal.orthanc.kt.discord.modules.threadUpdate

import com.rpgportugal.orthanc.kt.discord.domain.archive.ArchiveState
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.persistence.dto.module.ThreadUpdateConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.GenericChannelEvent
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent

class ThreadUpdateListenerAdapter(
    private val jda: JDA,
    private val threadUpdateConfiguration: ThreadUpdateConfiguration
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        }catch (e: Exception) {
            return ThrowableError(e)
        }
    }

    override fun onChannelUpdateArchived(event: ChannelUpdateArchivedEvent) {
        doThreadChangedEvent(event) { thread ->
            val state =
                getArchiveState(event.oldValue ?: false, event.newValue ?: false)

            ":blue_book: Thread ${thread.name} (${thread.jumpUrl}) mudou o estado para $state"
        }
    }

    override fun onChannelCreate(event: ChannelCreateEvent) {
        doThreadChangedEvent(event) { thread ->
            ":green_book: Thread ${thread.name} (${thread.jumpUrl}) foi CRIADO"
        }
    }

    override fun onChannelDelete(event: ChannelDeleteEvent) {
        doThreadChangedEvent(event) { thread ->
            ":closed_book: Thread ${thread.name} (${thread.jumpUrl}) foi APAGADO"
        }
    }

    private fun doThreadChangedEvent(event: GenericChannelEvent, messageFn: (thread: ThreadChannel) -> String) {
        if (!event.channel.type.isThread) return // It's not a thread

        val warningChannelId: Long = threadUpdateConfiguration.warningChannelId
        val warningChannel = event.jda.getTextChannelById(warningChannelId)
        val thread = event.channel.asThreadChannel()

        warningChannel?.sendMessage(messageFn(thread))?.queue()
    }

    private fun getArchiveState(old: Boolean, new: Boolean): ArchiveState {
        return when {
            old && !new -> ArchiveState.Unarchived
            !old && new -> ArchiveState.Archived
            else -> ArchiveState.Impossible
        }
    }
}