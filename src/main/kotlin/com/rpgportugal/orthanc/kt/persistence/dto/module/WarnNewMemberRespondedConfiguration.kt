package com.rpgportugal.orthanc.kt.persistence.dto.module

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Check

@Entity
@Table(name = "otc_warn_new_member_responded_configuration")
open class WarnNewMemberRespondedConfiguration {

    @Id
    @Column(name = "id", nullable = false)
    @Check(name = "one_row", constraints = "id = 1")
    open var id: Long = 1L

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    @Column(name = "moderator_role_id", nullable = false)
    open var moderatorRoleId: Long = 0

    @Column(name = "arrival_channel_id", nullable = false)
    open var arrivalChannelId: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WarnNewMemberRespondedConfiguration) return false

        if (id != other.id) return false
        if (warningChannelId != other.warningChannelId) return false
        if (moderatorRoleId != other.moderatorRoleId) return false
        if (arrivalChannelId != other.arrivalChannelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + warningChannelId.hashCode()
        result = 31 * result + moderatorRoleId.hashCode()
        result = 31 * result + arrivalChannelId.hashCode()
        return result
    }


}