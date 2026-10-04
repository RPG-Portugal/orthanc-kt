package com.rpgportugal.orthanc.kt.persistence.sql.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableRepository
import com.rpgportugal.orthanc.kt.persistence.sql.SqlCrudRepository
import jakarta.persistence.EntityManager

class SqlCollectableRepository(
    entityManager: EntityManager
) : SqlCrudRepository<Collectable, Long>(entityManager, Collectable::class), CollectableRepository
