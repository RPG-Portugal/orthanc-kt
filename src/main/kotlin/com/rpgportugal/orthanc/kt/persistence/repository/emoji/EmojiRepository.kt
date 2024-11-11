package com.rpgportugal.orthanc.kt.persistence.repository.emoji

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.domain.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji


interface EmojiRepository {
    fun getEmojiByCategory(category: EmojiCategory): Either<DbError, List<Emoji>>
}