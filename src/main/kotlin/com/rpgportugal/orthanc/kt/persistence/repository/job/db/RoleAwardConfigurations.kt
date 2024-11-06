package com.rpgportugal.orthanc.kt.persistence.repository.job.db

import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.db.Emojis
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.varchar

object RoleAwardConfigurations : Table<RoleAwardConfiguration>("role_award_configuration") {
    val roleId = long("role_id").primaryKey().bindTo { it.roleId }
    val adminAwardRole = long("admin_award_role").bindTo { it.adminAwardRole }
    val threshold = long("threshold").bindTo { it.threshold }
    val warningChannelId = long("warning_channel_id").bindTo { it.warningChannelId }
    val emoji = varchar("emoji_key").references(Emojis) { it.emojiKey }
    val jobName = varchar("job_id").references(JobConfigurations) { it.jobConfiguration }
}