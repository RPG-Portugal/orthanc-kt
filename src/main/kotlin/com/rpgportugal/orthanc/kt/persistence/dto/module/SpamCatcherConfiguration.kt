package com.rpgportugal.orthanc.kt.persistence.dto.module

import com.rpgportugal.orthanc.kt.persistence.dto.job.JobConfiguration
import jakarta.persistence.*
import org.hibernate.annotations.Check

@Entity
@Table(name = "otc_spam_catcher_configuration")
open class SpamCatcherConfiguration {

    @Id
    @Column(name = "id", nullable = false)
    @Check(name = "one_row", constraints = "id = 1")
    open var id: Long = 1L

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_configuration_id", nullable = false)
    open var jobConfiguration: JobConfiguration = JobConfiguration()

    @Column(name = "linkRegex", nullable = false)
    open var linkRegex: String = ""

    @Column(name = "honeypot_channel_id", nullable = false)
    open var honeypotChannelId: Long = 0

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpamCatcherConfiguration) return false

        if (id != other.id) return false
        if (jobConfiguration != other.jobConfiguration) return false
        if (linkRegex != other.linkRegex) return false
        if (honeypotChannelId != other.honeypotChannelId) return false
        if (warningChannelId != other.warningChannelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + jobConfiguration.hashCode()
        result = 31 * result + linkRegex.hashCode()
        result = 31 * result + honeypotChannelId.hashCode()
        result = 31 * result + warningChannelId.hashCode()
        return result
    }


}