package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "col_collectable_item",
)
class CollectableItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "discord_user_id", nullable = false)
    var discordUserId: String = "",

    @ManyToOne(optional = false)
    @JoinColumn(name = "collectable_id", nullable = false)
    var collectable: Collectable? = null,

    @Column(name = "acquired_at", nullable = false, updatable = false)
    var acquiredAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "variance")
    var variance: String = "",
)