package com.rpgportugal.orthanc.kt.persistence.dto.job

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "otk_job_configuration")
open class JobConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = 0

    @Column(name = "name", nullable = false)
    open var name: String = ""

    @Column(name = "scheduler_group_name", nullable = false)
    open var schedulerGroupName: String = ""

    @Column(name = "trigger_name", nullable = false)
    open var triggerName: String = ""

    @Column(name = "cron_expression", nullable = false)
    open var cronExpression: String = ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JobConfiguration) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (schedulerGroupName != other.schedulerGroupName) return false
        if (triggerName != other.triggerName) return false
        if (cronExpression != other.cronExpression) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + schedulerGroupName.hashCode()
        result = 31 * result + triggerName.hashCode()
        result = 31 * result + cronExpression.hashCode()
        return result
    }


}