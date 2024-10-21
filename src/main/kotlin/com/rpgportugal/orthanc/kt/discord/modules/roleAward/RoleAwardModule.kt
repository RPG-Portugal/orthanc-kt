package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RoleAwardModule() : ListenerAdapter(), BotModule, KoinComponent {

    override val propertiesLoader: PropertiesLoader by inject<PropertiesLoader>()
    override val propertiesEither = propertiesLoader.load("env/roleAwardModule.properties")

    var roleId: String? = null
    var threshold: Int = 99
    var emojiNames: String? = null
    var warningChannelID: String? = null

    override fun getName(): String = "Role Award"

    override fun attach(jda: JDA) {
        jda.addEventListener(this)
        roleId = when (propertiesEither) {
            is Either.Left -> null
            is Either.Right -> propertiesEither.value.getProperty("roleId")
        }
        warningChannelID = when (propertiesEither) {
            is Either.Left -> null
            is Either.Right -> propertiesEither.value.getProperty("warningChannelId")
        }
        emojiNames = when (propertiesEither) {
            is Either.Left -> null
            is Either.Right -> propertiesEither.value.getProperty("emojiNames")
        }
        threshold = when (propertiesEither) {
            is Either.Left -> 99
            is Either.Right -> propertiesEither.value.getProperty("threshold")?.toInt() ?: 99
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
        }

    }

}