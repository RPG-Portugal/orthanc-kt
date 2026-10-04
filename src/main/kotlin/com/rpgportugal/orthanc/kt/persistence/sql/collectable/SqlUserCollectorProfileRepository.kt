package com.rpgportugal.orthanc.kt.persistence.sql.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.UserCollectorProfile
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.UserCollectorProfileRepository
import com.rpgportugal.orthanc.kt.persistence.sql.SqlCrudRepository
import jakarta.persistence.EntityManager

class SqlUserCollectorProfileRepository(
    entityManager: EntityManager
) : SqlCrudRepository<UserCollectorProfile, String>(entityManager, UserCollectorProfile::class),
    UserCollectorProfileRepository