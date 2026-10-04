package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "col_booster_slot")
class BoosterSlot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "booster_type_id", nullable = false)
    var boosterType: BoosterType? = null,

    @Column(name = "slot_number", nullable = false)
    var slotNumber: Int = 0,

    @Column(name = "card_count", nullable = false)
    var cardCount: Int = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "required_rarity_tier_id", nullable = false)
    var requiredRarityTier: RarityTier? = null,

    @Column(name = "is_flexible", nullable = false)
    var isFlexible: Boolean = false,

    @Column(name = "weight", nullable = false)
    var weight: Double = 1.0,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(targetEntity = Collectable::class)
    @JoinTable(
        name = "col_booster_slot_collectable",
        joinColumns = [JoinColumn(name = "booster_slot_id")],
        inverseJoinColumns = [JoinColumn(name = "collectable_id")]
    )
    var collectables: MutableList<Collectable> = mutableListOf()
)