package com.rpgportugal.orthanc.kt.persistence.dto.app

import jakarta.persistence.*

@Entity
@Table(name = "okt_active_application")
open class ActiveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = 1

    @Column(name = "active", nullable = false)
    open var active: Boolean = false

    @JoinColumn(name = "app_id", referencedColumnName = "id", nullable = false)
    @OneToOne(fetch = FetchType.EAGER)
    open var application: Application = Application()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActiveApplication) return false

        if (id != other.id) return false
        if (active != other.active) return false
        if (application != other.application) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + application.hashCode()
        return result
    }


}