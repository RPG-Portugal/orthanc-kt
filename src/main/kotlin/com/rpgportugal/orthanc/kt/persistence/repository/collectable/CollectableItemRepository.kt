package com.rpgportugal.orthanc.kt.persistence.repository.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.CollectableItem
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.repository.CrudRepository
import arrow.core.Either

interface CollectableItemRepository : CrudRepository<CollectableItem, Long> {
    fun findByDiscordUserId(discordUserId: String): Either<DbError, List<CollectableItem>>
}