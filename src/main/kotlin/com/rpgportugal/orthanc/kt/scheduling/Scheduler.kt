package com.rpgportugal.orthanc.kt.scheduling

import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.Trigger
import java.util.*

interface Scheduler {

    fun scheduleJob(jobDetail: JobDetail, trigger: Trigger): Date?

    fun simpleCronJobSchedule(
        jobName: String,
        triggerName: String,
        groupName: String,
        cron: String,
        jobClass: Class<out Job>,
        jobData: JobDataMap,
    ): Date?
}