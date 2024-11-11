package com.rpgportugal.orthanc.kt.persistence.dto.module

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Check


@Entity
@Table(name = "otc_thread_update")
open class ThreadUpdateConfiguration {

    @Id
    @Column(name = "id", nullable = false)
    @Check(name = "one_row", constraints = "id = 1")
    open var id: Long = 1L

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ThreadUpdateConfiguration) return false

        if (id != other.id) return false
        if (warningChannelId != other.warningChannelId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + warningChannelId.hashCode()
        return result
    }


}