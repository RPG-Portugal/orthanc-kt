package com.rpgportugal.orthanc.kt.discord.modules.dice

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.DiceModuleError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import dev.diceroll.parser.detailedRoll
import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.minn.jda.ktx.interactions.components.getOption
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

class DiceModule(
    propertiesLoader: PropertiesLoader,
    private val emojiRepository: EmojiRepository
) : ListenerAdapter(), BotModule {

    companion object : DepModule {
        override val module = module {
            single { DiceModule(get(), get()) }
        }
    }

    override val propertiesEither = propertiesLoader.load("env/diceModule.properties")

    private var onRoll: CoroutineEventListener? = null

    private lateinit var diceMap: Map<String, String>

    override fun getName(): String = "Dice Roll"

    override fun attach(jda: JDA) {

        when (val emojis = emojiRepository.getEmojiKeyToDiscordCodeMap()) {
            is Either.Right -> diceMap = emojis.value
            is Either.Left -> {
                log.error("Failed to get emojis: {}", emojis.value)
                when (val err = emojis.value) {
                    is DbError.EntityNotFoundError<*> ->
                        throw Exception("${err.entityName} - ${err.id} - ${err.message}")
                    is ThrowableError<*> ->
                        throw err.exception
                }
            }
        }

        onRoll = jda.onCommand("roll", timeout = 2.seconds) { event ->
            val formula = event.getOption<String>("formula") ?: ""

            doRoll(formula, event.user.effectiveName, jda) {
                event.reply(it).queue()
            }
        }

        jda.addEventListener(this)

        jda.updateCommands {
            slash("roll", "rola todos os dados") {
                option<String>("formula", "The dice formula to roll", autocomplete = true, required = true)
            }
        }.queue()

    }

    override fun detach(jda: JDA) {
        onRoll?.cancel()
        onRoll = null
        jda.removeEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message.contentStripped
        if (!message.lowercase().startsWith("\$roll")) return

        val messageTokens = message.split(" ")
        if (messageTokens.size < 2) return

        val formula = messageTokens.subList(1, messageTokens.size).joinToString(" ")

        doRoll(formula, event.author.effectiveName, event.jda) {
            event.message.reply(it).queue()
        }

    }

    private fun doRoll(formula: String, userName: String, jda: JDA, sendReply: (String) -> Unit) {
        val rollResult = Either.catch {
            detailedRoll(formula)
        }.mapLeft {
            DiceModuleError.DiceParsingError(formula, it.message ?: "Error parsing the formula.")
        }

        when (rollResult) {
            is Either.Left -> {
                val error = rollResult.value
                sendReply("$userName Error rolling ${error.formula} => ${error.message}.\nCheck this link for more information: https://github.com/diceroll-dev/dice-parser/tree/main?tab=readme-ov-file#supported-notation")
            }

            is Either.Right -> {
                val resultTree = rollResult.value
                var prettyText = resultTree.prettyPrint()

                diceMap.mapValues {
                    val diceEmojiId = it.value
                    val default = "[${it.key}]"
                    try {
                        val diceEmoji = jda.getEmojiById(diceEmojiId)
                        diceEmoji?.asMention ?: default
                    } catch (_: Exception) {
                        default
                    }
                }.forEach {
                    prettyText = prettyText.replace("[${it.key}]", it.value)
                }

                sendReply("O resultado é: $prettyText")
            }
        }
    }

}