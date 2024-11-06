package com.rpgportugal.orthanc.kt.persistence.repository.emoji

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError

interface EmojiRepository {
    fun getEmojiKeyToDiscordCodeMap(): Either<DbError, Map<String, String>>
}