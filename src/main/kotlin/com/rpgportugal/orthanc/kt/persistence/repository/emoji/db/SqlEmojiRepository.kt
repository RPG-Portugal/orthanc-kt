package com.rpgportugal.orthanc.kt.persistence.repository.emoji.db

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.CategoryId
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

class SqlEmojiRepository(private val entityManager: EntityManager) : Logging, EmojiRepository {
    override fun getEmojiByCategory(category: CategoryId): Either<DbError, EmojiCategory> =
        try {
            val query =
                "select ECAT from EmojiCategory ECAT " +
                        "join fetch Emoji EM " +
                        "where ECAT.id = :category"

            val result =
                entityManager
                    .createQuery(query, EmojiCategory::class.java)
                    .setParameter(":category", category)
                    .singleResult

            Either.Right(result)
        } catch (e: NonUniqueResultException) {
            log.error("Failed to get emoji category", e)
            Either.Left(
                DbError.EntityNotFoundError(
                    EmojiCategory::class.java.name,
                    category,
                    e.message ?: "Failed to find EmojiCategory with id = $category"
                )
            )
        } catch (e: NoResultException) {
            log.error("Failed to get emoji category", e)
            Either.Left(
                DbError.EntityNotUnique(
                    EmojiCategory::class.java.name,
                    category,
                    e.message ?: "Too many EmojiCategory with id = $category"
                )
            )
        } catch (e: Exception) {
            log.error("Error getting emoji from repository", e)
            Either.Left(ThrowableError(e))
        }

}