package com.rpgportugal.orthanc.kt.discord.modules.dice

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.domain.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.util.TryCloseable
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import net.dv8tion.jda.api.JDA

class DiceModule(
    private val jda: JDA,
    private val emojiRepository: EmojiRepository,
) : BotModule, Loggable {

    override fun getName(): String = "dice-roll"

    override fun start(): Either<DomainError, TryCloseable> {
        try {
            val emojis = when (val result = emojiRepository.getEmojiByCategory(EmojiCategory.Dice)) {
                is Either.Right -> result.value.emoji.associate { it.key to it.discordId }
                is Either.Left -> {
                    log.error("start - Failed to get emojis: {}", result.value)
                    return Either.Left(result.value)
                }
            }

            val listenerAdapter = DiceListenerAdapter(jda, emojis)

            jda.addEventListener(this)

            jda.updateCommands {
                slash("roll", "rola todos os dados") {
                    option<String>("formula", "The dice formula to roll", autocomplete = true, required = true)
                }
            }.queue()

            return Either.Right(listenerAdapter)

        } catch (e: Exception) {
            log.error("start - failed to initialize DiceModule", e)
            return Either.Left(ThrowableError(e))
        }
    }
}