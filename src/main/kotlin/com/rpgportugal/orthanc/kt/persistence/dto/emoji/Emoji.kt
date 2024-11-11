package com.rpgportugal.orthanc.kt.persistence.dto.emoji

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name="emoji")
open class Emoji {

    @Id
    @Column(name="key", nullable = false)
    open var key: String = ""

    @Column(name="name", nullable = false)
    open var name: String = ""

    @Column(name="discord_id", nullable = false)
    open var discordId: String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Emoji) return false

        if (key != other.key) return false
        if (name != other.name) return false
        if (discordId != other.discordId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + discordId.hashCode()
        return result
    }


}