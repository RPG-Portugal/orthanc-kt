package com.rpgportugal.orthanc.kt.persistence.repository.emoji.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import jakarta.persistence.EntityManager

class SqlEmojiRepository(private val entityManager: EntityManager) : Logging, EmojiRepository {
    override fun getEmojiKeyToDiscordCodeMap(): Either<DbError, Map<String, String>> =
        try {
            TODO()
        } catch (e: Exception) {
            log.error("Error getting emoji from repository", e)
            Either.Left(ThrowableError(e))
        }

}