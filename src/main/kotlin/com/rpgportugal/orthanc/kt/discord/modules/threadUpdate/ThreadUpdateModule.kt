package com.rpgportugal.orthanc.kt.discord.modules.threadUpdate

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.discord.ArchiveState
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.GenericChannelEvent
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateArchivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent

class ThreadUpdateModule(propertiesLoader: PropertiesLoader) : ListenerAdapter(), BotModule, KoinComponent {

    override val propertiesEither = propertiesLoader.load("env/threadUpdateModule.properties")

    override fun getName(): String = "Thread Update"

    override fun attach(jda: JDA) {
        jda.addEventListener(this)
    }

    override fun detach(jda: JDA) {
        jda.removeEventListener(this)
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
        val warningChannelId = when (propertiesEither) {
            is Either.Left -> return // No warning channel defined.
            is Either.Right -> propertiesEither.value.getProperty("warningChannelId") ?: return
        }
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