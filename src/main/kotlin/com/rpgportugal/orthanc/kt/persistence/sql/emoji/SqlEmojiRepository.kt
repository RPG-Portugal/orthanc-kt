package com.rpgportugal.orthanc.kt.persistence.sql.emoji

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.domain.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toLeft
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toRight
import jakarta.persistence.EntityManager

class SqlEmojiRepository(private val entityManager: EntityManager) : Loggable, EmojiRepository {
    override fun getEmojiByCategory(category: EmojiCategory): Either<DbError, List<Emoji>> {
        val query =
            "select EM from Emoji EM where EM.category = :category"

        return try {
            val result =
                entityManager
                    .createQuery(query, Emoji::class.java)
                    .setParameter("category", category)
                    .resultList

            result.toRight()
        } catch (e: Exception) {
            ThrowableError(e).toLeft()
        }
    }

}