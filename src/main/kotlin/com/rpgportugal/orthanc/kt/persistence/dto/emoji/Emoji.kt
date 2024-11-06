package com.rpgportugal.orthanc.kt.persistence.dto.emoji

import org.ktorm.entity.Entity

interface Emoji : Entity<Emoji> {
    companion object : Entity.Factory<Emoji>()

    val name: String
    val emojiKey: String
    val discordId: String
}