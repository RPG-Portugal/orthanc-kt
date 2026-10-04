package com.rpgportugal.orthanc.kt.discord.modules.collector.dto

import jakarta.persistence.*

@Entity
@Table(name = "col_caderneta")
class Caderneta (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @Column(name = "description", nullable = false)
    var description: String = "",

    @Column(name = "enabled", nullable = false)
    var enabled: Boolean = true,

    @ManyToMany(targetEntity = Collectable::class)
    @JoinTable(
        name = "col_caderneta_collectable",
        joinColumns = [JoinColumn(name = "caderneta_id")],
        inverseJoinColumns = [JoinColumn(name = "collectable_id")]
    )
    var collectables: List<Collectable> = emptyList(),

    )