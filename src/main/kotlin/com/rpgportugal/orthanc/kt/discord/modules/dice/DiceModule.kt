package com.rpgportugal.orthanc.kt.discord.modules.dice

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DiceParsingError
import com.rpgportugal.orthanc.kt.error.MissingPropertyError
import com.rpgportugal.orthanc.kt.logging.log
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.seconds

class DiceModule : ListenerAdapter(), BotModule, KoinComponent {

    val propertiesLoader: PropertiesLoader by inject<PropertiesLoader>()
    val propertiesEither = propertiesLoader.load("env/diceModule.properties")

    var onRoll : CoroutineEventListener? = null

    override fun getName(): String = "Dice Module"

    override fun isEnabled(): Boolean {

        return when (propertiesEither) {
            is Either.Left -> false
            is Either.Right -> when (val enabledProperty = Either.catch {
                propertiesEither.value.getProperty("enabled") ?: throw Exception("Missing enabled property!")
            }.mapLeft {
                MissingPropertyError("enabled", it.message?:"")
            }) {
                is Either.Left -> {
                    log.error("Missing property ${enabledProperty.value.propertyName}.")
                    false
                }
                is Either.Right -> enabledProperty.value == "true"
            }
        }
    }

    override fun attach(jda: JDA) {
        onRoll = jda.onCommand("roll", timeout = 2.seconds) { event ->
            val formula = event.getOption<String>("formula") ?: ""

            doRoll(formula, event.user.effectiveName){
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
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message.contentStripped
        if(!message.lowercase().startsWith("\$roll")) return

        val messageTokens = message.split(" ")
        if(messageTokens.size < 2) return

        val formula = messageTokens[1]

        doRoll(formula, event.author.effectiveName){
            event.channel.sendMessage(it).queue()
        }

    }

    fun doRoll (formula: String, userName: String, sendReply: (String) -> Unit ) {
        val rollResult = Either.catch {
            detailedRoll(formula)
        }.mapLeft {
            DiceParsingError(formula, it.message ?: "Error parsing the formula.")
        }

        when (rollResult) {
            is Either.Left -> {
                val error = rollResult.value
                sendReply("$userName Error rolling ${error.formula} => ${error.message}.\nCheck this link for more information: https://github.com/diceroll-dev/dice-parser/tree/main?tab=readme-ov-file#supported-notation")
            }

            is Either.Right -> {
                val resultTree = rollResult.value
                val flatResults = resultTree.results.map { it.value.toString() }.reduce { acc, i -> "$acc,$i" }
                sendReply("$userName rolled: [$flatResults] => ${resultTree.value}")
            }
        }
    }

}