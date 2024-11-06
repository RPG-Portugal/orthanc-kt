package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object RoleAwardConfigurations : Table<RoleAwardConfiguration>("role_award_configuration") {
    val roleId = varchar("role_id").primaryKey()
    val adminAwardRole = varchar("admin_award_role")
    val threshold = long("threshold")
    val warningChannelId = varchar("warning_channel_id")
}