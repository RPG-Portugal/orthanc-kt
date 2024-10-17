package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import com.rpgportugal.orthanc.kt.persistence.dto.Application
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Applications : Table<Application>("application") {
    val id = long("id").primaryKey().bindTo { it.id }
    val token = varchar("token").bindTo { it.token }
}