package com.rpgportugal.orthanc.kt.persistence.repository.application.db

import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object Applications : Table<Application>("application") {
    val id = long("id").primaryKey().bindTo { it.id }
    val name = varchar("name").bindTo { it.name }
    val token = varchar("token").bindTo { it.token }
}