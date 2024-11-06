package com.rpgportugal.orthanc.kt.persistence.dto.app

import org.ktorm.entity.Entity

interface Application : Entity<Application> {
    companion object : Entity.Factory<Application>()

    val name: String
    val id: Long
    val token: String
}