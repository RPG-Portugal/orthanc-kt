package com.rpgportugal.orthanc.kt.persistence.sql.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.BoosterType
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.BoosterTypeRepository
import com.rpgportugal.orthanc.kt.persistence.sql.SqlCrudRepository
import jakarta.persistence.EntityManager

class SqlBoosterTypeRepository (
    entityManager: EntityManager
) : SqlCrudRepository<BoosterType, Long>(entityManager, BoosterType::class), BoosterTypeRepository
