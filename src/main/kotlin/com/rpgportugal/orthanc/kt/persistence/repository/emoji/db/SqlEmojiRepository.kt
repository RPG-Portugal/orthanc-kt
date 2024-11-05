package com.rpgportugal.orthanc.kt.persistence.repository.emoji.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.emoji.EmojiKey
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import org.ktorm.database.Database
import org.ktorm.entity.map
import org.ktorm.entity.sequenceOf

class SqlEmojiRepository(private val database: Database) : Logging, EmojiRepository {
    override fun getEmojiKeyToDiscordCodeMap(): Either<DbError,Map<EmojiKey, String>> =
        try {
            val result =
                database.sequenceOf(Emojis)
                    .map { it.emojiKey to it.discordId }
                    .toMap()

            Either.Right(result)
        }catch (e : Exception) {
            log.error("Error getting emoji from repository", e)
            Either.Left(ThrowableError(e))
        }

}