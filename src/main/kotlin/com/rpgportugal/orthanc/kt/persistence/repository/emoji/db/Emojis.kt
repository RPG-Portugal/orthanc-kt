package com.rpgportugal.orthanc.kt.persistence.repository.emoji.db

import com.rpgportugal.orthanc.kt.discord.emoji.EmojiKey
import com.rpgportugal.orthanc.kt.persistence.dto.Emoji
import org.ktorm.schema.Table
import org.ktorm.schema.enum
import org.ktorm.schema.varchar

object Emojis : Table<Emoji>("emojis") {
    val emoji_key = enum<EmojiKey>("emoji_key").primaryKey().bindTo { it.emojiKey }
    val discordId = varchar("discord_id").primaryKey().bindTo { it.discordId }
}