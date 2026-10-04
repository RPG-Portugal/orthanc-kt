package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "col_collectable")
class Collectable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String = "",

    @ManyToOne(optional = false)
    @JoinColumn(name = "rarity_tier_id")
    var rarityTier: RarityTier? = null,

    @Column(name = "system_type")
    var systemType: String = "CUSTOM",

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "url")
    var url: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)