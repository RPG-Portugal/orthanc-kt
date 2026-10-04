package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "col_rarity_tier")
class RarityTier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false, unique = true)
    var name: String = "",

    @Column(name = "color", nullable = false)
    var color: String = "",

    @Column(name = "drop_odds", nullable = false)
    var dropOdds: Double = 0.0,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int = 0,

    @Column(name = "description")
    var description: String = "",

    @Column(name = "display_symbol")
    var displaySymbol: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)