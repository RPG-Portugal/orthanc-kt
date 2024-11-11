package com.rpgportugal.orthanc.kt.persistence.repository.emoji

import arrow.core.Either

import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.CategoryId
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.EmojiCategory


interface EmojiRepository {
    fun getEmojiByCategory(category: CategoryId): Either<DbError, EmojiCategory>
}