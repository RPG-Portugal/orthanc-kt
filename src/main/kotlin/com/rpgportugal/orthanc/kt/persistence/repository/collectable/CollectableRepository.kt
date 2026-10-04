package com.rpgportugal.orthanc.kt.persistence.repository.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import com.rpgportugal.orthanc.kt.persistence.repository.CrudRepository

interface CollectableRepository : CrudRepository<Collectable, Long>
