package com.rpgportugal.orthanc.kt.persistence.dto

import org.ktorm.entity.Entity

interface Emoji : Entity<Emoji> {
    
    val emoji: com.rpgportugal.orthanc.kt.discord.emoji.Emoji
    val discordId: String
}