package com.rpgportugal.orthanc.kt.persistence.dto.job

import org.ktorm.entity.Entity

interface JobConfiguration : Entity<JobConfiguration> {
    companion object : Entity.Factory<JobConfiguration>()

    val schedulerGroupName: String
    val triggerName: String
    val jobName : String
    val cronExpression : String
}