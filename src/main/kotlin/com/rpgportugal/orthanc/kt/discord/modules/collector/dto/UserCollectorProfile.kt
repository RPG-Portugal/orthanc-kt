package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "col_user_collector_profile")
class UserCollectorProfile(
    @Id // discord user id
    var id: String = "0",

    @Column(name = "last_booster_pulled_at")
    var lastBoosterPulledAt: LocalDateTime? = null,

    @Column(name = "total_boosters_pulled", nullable = false)
    var totalBoostersPulled: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)