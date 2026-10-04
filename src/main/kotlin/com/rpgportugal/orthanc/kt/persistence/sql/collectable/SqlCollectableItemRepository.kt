package com.rpgportugal.orthanc.kt.persistence.sql.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.CollectableItem
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import com.rpgportugal.orthanc.kt.persistence.sql.SqlCrudRepository
import arrow.core.Either
import jakarta.persistence.EntityManager

class SqlCollectableItemRepository(
    entityManager: EntityManager
) : SqlCrudRepository<CollectableItem, Long>(entityManager, CollectableItem::class), CollectableItemRepository {

    override fun findByDiscordUserId(discordUserId: String): Either<DbError, List<CollectableItem>> = try {
        val query = entityManager.createQuery(
            "FROM CollectableItem WHERE discordUserId = :discordUserId ORDER BY acquiredAt DESC",
            CollectableItem::class.java
        )
        query.setParameter("discordUserId", discordUserId)
        Either.Right(query.resultList)
    } catch (e: Exception) {
        Either.Left(DbError.Unknown(e.message ?: "Unknown error"))
    }
}