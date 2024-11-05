package com.rpgportugal.orthanc.kt.persistence.dto

import org.ktorm.entity.Entity

interface Emoji : Entity<Emoji> {
    val emojiKey: String
    val discordId: String
}