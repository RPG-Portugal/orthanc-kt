package com.rpgportugal.orthanc.kt.persistence.repository.emoji

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.emoji.EmojiKey
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.Emoji

interface EmojiRepository {
    fun getEmojiKeyToDiscordCodeMap(): Either<DbError, Map<String, String>>
}