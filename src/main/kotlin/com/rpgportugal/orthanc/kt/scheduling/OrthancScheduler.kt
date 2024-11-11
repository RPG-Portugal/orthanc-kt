package com.rpgportugal.orthanc.kt.scheduling

import arrow.core.Either
import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.error.SchedulerError
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toLeft
import com.rpgportugal.orthanc.kt.util.TryCloseable
import org.koin.dsl.bind
import org.koin.dsl.module
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.util.*

class OrthancScheduler : Scheduler {
    private val quartzScheduler = StdSchedulerFactory().scheduler

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
    ): Either<SchedulerError, TryCloseable> {
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

        val scheduleJob = scheduleJob(jobDetail, trigger)

        return if (scheduleJob != null) {
            SchedulerError.FailedToSchedule(jobName).toLeft()
        } else {
            TryCloseable {
                val isClosed = quartzScheduler.unscheduleJob(trigger.key)
                if (isClosed) {
                    null
                } else {
                    SchedulerError.FailedToUnschedule(jobName)
                }
            }.asResult()
        }
    }

    companion object : DepModule {
        override val module = module {
            single { OrthancScheduler() } bind Scheduler::class
        }
    }
}