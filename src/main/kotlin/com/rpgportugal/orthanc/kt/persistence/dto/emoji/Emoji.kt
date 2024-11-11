package com.rpgportugal.orthanc.kt.persistence.dto.emoji

import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import jakarta.persistence.*

@Entity
@Table(name = "otk_emoji")
open class Emoji {

    @Id
    @Column(name = "key", nullable = false)
    open var key: String = ""

    @Column(name = "discord_id", nullable = false)
    open var discordId: String = ""

    @Column(name = "name", nullable = false)
    open var name: String = ""

    @ManyToOne(targetEntity = RoleAwardConfiguration::class, fetch = FetchType.EAGER, optional = true)
    open var roleAwardConfigurationId: RoleAwardConfiguration? = null

    @ManyToOne(targetEntity = EmojiCategory::class, fetch = FetchType.EAGER, optional = false)
    open var category: EmojiCategory = EmojiCategory()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Emoji) return false

        if (key != other.key) return false
        if (discordId != other.discordId) return false
        if (name != other.name) return false
        if (roleAwardConfigurationId != other.roleAwardConfigurationId) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + discordId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (roleAwardConfigurationId?.hashCode() ?: 0)
        result = 31 * result + category.hashCode()
        return result
    }


}