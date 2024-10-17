package com.rpgportugal.orthanc.kt.persistence.dto

import org.ktorm.entity.Entity

interface Application : Entity<Application> {
    val id: Long
    val token: String
}