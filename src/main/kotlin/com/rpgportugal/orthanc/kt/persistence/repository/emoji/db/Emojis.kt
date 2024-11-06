package com.rpgportugal.orthanc.kt.persistence.repository.emoji.db

import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji
import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object Emojis : Table<Emoji>("emoji") {
    val emoji_key = varchar("emoji_key").primaryKey().bindTo { it.emojiKey }
    val name = varchar("name").bindTo { it.name }
    val discordId = varchar("discord_id").bindTo { it.discordId }
}