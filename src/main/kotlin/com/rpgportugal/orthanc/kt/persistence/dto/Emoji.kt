package com.rpgportugal.orthanc.kt.persistence.dto

import com.rpgportugal.orthanc.kt.discord.emoji.EmojiKey
import org.ktorm.entity.Entity

interface Emoji : Entity<Emoji> {
    val emojiKey: EmojiKey
    val discordId: String
}