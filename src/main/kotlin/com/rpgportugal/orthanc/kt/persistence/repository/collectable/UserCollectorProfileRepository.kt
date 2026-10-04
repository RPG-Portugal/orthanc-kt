package com.rpgportugal.orthanc.kt.persistence.repository.collectable

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.UserCollectorProfile
import com.rpgportugal.orthanc.kt.persistence.repository.CrudRepository

interface UserCollectorProfileRepository : CrudRepository<UserCollectorProfile, String>