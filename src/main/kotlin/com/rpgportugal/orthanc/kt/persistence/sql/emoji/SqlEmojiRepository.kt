package com.rpgportugal.orthanc.kt.persistence.sql.emoji

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.CategoryId
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException

class SqlEmojiRepository(private val entityManager: EntityManager) : Loggable, EmojiRepository {
    override fun getEmojiByCategory(category: CategoryId): Either<DbError, EmojiCategory> {
        val query =
            "select ECAT from EmojiCategory ECAT " +
                    "join fetch Emoji EM " +
                    "where ECAT.id = :category"

        val typedQuery =
            entityManager
                .createQuery(query, EmojiCategory::class.java)
                .setParameter(":category", category)

        return QueryUtil.getSingleIdValue(typedQuery, EmojiCategory:: class.java)
    }

}