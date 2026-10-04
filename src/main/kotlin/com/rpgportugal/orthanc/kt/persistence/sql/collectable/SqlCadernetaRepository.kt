package com.rpgportugal.orthanc.kt.persistence.sql.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Caderneta
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CadernetaRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableRepository
import com.rpgportugal.orthanc.kt.persistence.sql.SqlCrudRepository
import jakarta.persistence.EntityManager

class SqlCadernetaRepository(
    entityManager: EntityManager
) : SqlCrudRepository<Caderneta, Long>(entityManager, Caderneta::class), CadernetaRepository
