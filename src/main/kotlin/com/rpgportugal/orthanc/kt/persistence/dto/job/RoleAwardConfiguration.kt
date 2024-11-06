package com.rpgportugal.orthanc.kt.persistence.dto.job

import org.ktorm.entity.Entity

interface RoleAwardConfiguration : Entity<RoleAwardConfiguration> {
    companion object : Entity.Factory<RoleAwardConfiguration>()

    val roleId: Long
    val adminAwardRole: Long
    val threshold: Long
    val warningChannelId: Long

}