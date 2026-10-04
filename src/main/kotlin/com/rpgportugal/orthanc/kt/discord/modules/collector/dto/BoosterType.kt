package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "col_booster_type")
class BoosterType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description")
    var description: String = "",

    @Column(name = "card_count", nullable = false)
    var cardCount: Int = 0,

    @Column(name = "cost", nullable = false)
    var cost: Int = 0,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @OneToMany(mappedBy = "boosterType", cascade = [CascadeType.ALL], orphanRemoval = true)
    var slots: MutableList<BoosterSlot> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)