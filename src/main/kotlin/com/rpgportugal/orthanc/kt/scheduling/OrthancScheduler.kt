package com.rpgportugal.orthanc.kt.scheduling

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import org.koin.dsl.bind
import org.koin.dsl.module
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.util.*

class OrthancScheduler : Scheduler {
    val quartzScheduler = StdSchedulerFactory().scheduler

    init {
        quartzScheduler.start()
    }

    override fun scheduleJob(jobDetail: JobDetail, trigger: Trigger): Date? {
        return quartzScheduler.scheduleJob(jobDetail, trigger)
    }

    override fun simpleCronJobSchedule(
        jobName: String,
        triggerName: String,
        groupName: String,
        cron: String,
        jobClass: Class<out Job>,
        jobData: JobDataMap,
    ): Date? {
        val jobBuilder = JobBuilder.newJob(jobClass)

        val jobDetail = jobBuilder
            .usingJobData(jobData)
            .withIdentity(jobName, groupName)
            .build()
        val trigger = TriggerBuilder.newTrigger()
            .withIdentity(triggerName, groupName)
            .startAt(Date())
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build()
        return scheduleJob(jobDetail, trigger)
    }

    companion object : DepModule {
        override val module = module {
            single { OrthancScheduler() } bind Scheduler::class
        }
    }
}