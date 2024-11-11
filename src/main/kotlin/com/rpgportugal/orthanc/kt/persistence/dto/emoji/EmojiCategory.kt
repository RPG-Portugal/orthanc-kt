package com.rpgportugal.orthanc.kt.persistence.dto.emoji

import jakarta.persistence.*

typealias CategoryId = com.rpgportugal.orthanc.kt.discord.domain.emoji.EmojiCategory

@Entity
@Table(name = "dice_emoji")
open class EmojiCategory {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "id", nullable = false)
    open var id = CategoryId.Misc


    @OneToMany(fetch = FetchType.EAGER, targetEntity = Emoji::class)
    @JoinColumn(name = "emoji_id", nullable = false)
    open var emoji: List<Emoji> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EmojiCategory) return false

        if (id != other.id) return false
        if (emoji != other.emoji) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + emoji.hashCode()
        return result
    }


}