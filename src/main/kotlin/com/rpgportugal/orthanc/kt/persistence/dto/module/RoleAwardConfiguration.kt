package com.rpgportugal.orthanc.kt.persistence.dto.module

import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji
import com.rpgportugal.orthanc.kt.persistence.dto.job.JobConfiguration
import jakarta.persistence.*
import org.hibernate.annotations.Check

@Entity
@Table(name = "otc_role_award_configuration")

open class RoleAwardConfiguration {

    @Id
    @Column(name = "id", nullable = false)
    @Check(name = "one_row", constraints = "id = 1")
    open var id: Long = 1L

    @Column(name = "role_id", nullable = false)
    open var roleId: Long = 0

    @Column(name = "admin_award_role_id", nullable = false)
    open var adminAwardRoleId: Long = 0

    @Column(name = "threshold", nullable = false)
    open var threshold: Long = 0

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_configuration_id", nullable = false)
    open var jobConfiguration: JobConfiguration = JobConfiguration()

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "key", targetEntity = Emoji::class)
    open var emojis = mutableListOf<Emoji>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RoleAwardConfiguration) return false

        if (id != other.id) return false
        if (roleId != other.roleId) return false
        if (adminAwardRoleId != other.adminAwardRoleId) return false
        if (threshold != other.threshold) return false
        if (warningChannelId != other.warningChannelId) return false
        if (jobConfiguration != other.jobConfiguration) return false
        if (emojis != other.emojis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + roleId.hashCode()
        result = 31 * result + adminAwardRoleId.hashCode()
        result = 31 * result + threshold.hashCode()
        result = 31 * result + warningChannelId.hashCode()
        result = 31 * result + jobConfiguration.hashCode()
        result = 31 * result + emojis.hashCode()
        return result
    }


}