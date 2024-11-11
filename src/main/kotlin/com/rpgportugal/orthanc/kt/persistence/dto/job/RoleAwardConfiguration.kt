package com.rpgportugal.orthanc.kt.persistence.dto.job

import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji
import jakarta.persistence.*

@Entity
@Table(name = "role_award_configuration")
open class RoleAwardConfiguration : JobConfiguration() {

    @Column(name = "role_id", nullable = false)
    open var roleId: Long = 0

    @Column(name = "admin_award_role_id", nullable = false)
    open var adminAwardRoleId: Long = 0

    @Column(name = "threshold", nullable = false)
    open var threshold: Long =  0

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    @Column(name="emoji_id", nullable = false)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "key")
    open var emojis = mutableListOf<Emoji>()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RoleAwardConfiguration) return false

        if (roleId != other.roleId) return false
        if (adminAwardRoleId != other.adminAwardRoleId) return false
        if (threshold != other.threshold) return false
        if (warningChannelId != other.warningChannelId) return false
        if (emojis != other.emojis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roleId.hashCode()
        result = 31 * result + adminAwardRoleId.hashCode()
        result = 31 * result + threshold.hashCode()
        result = 31 * result + warningChannelId.hashCode()
        result = 31 * result + emojis.hashCode()
        return result
    }


}