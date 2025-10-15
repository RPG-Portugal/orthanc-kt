package com.rpgportugal.orthanc.kt.discord.modules.dice

import com.rpgportugal.dicegoblin.expressions.ExpressionResult
import com.rpgportugal.dicegoblin.expressions.dice.NumberDiceExpression
import com.rpgportugal.dicegoblin.expressions.modifiers.TargetModifier
import com.rpgportugal.dicegoblin.expressions.operators.AddOperatorExpression
import com.rpgportugal.dicegoblin.expressions.operators.SubOperatorExpression
import com.rpgportugal.dicegoblin.expressions.result.GroupRollResult
import com.rpgportugal.dicegoblin.expressions.result.RollResult
import com.rpgportugal.dicegoblin.parser.parse
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import dev.diceroll.parser.*
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.getOption
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.time.Duration.Companion.seconds

class ExperimentalDiceListenerAdapter(
    private val jda: JDA,
    private val diceMap: Map<String, String>,
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    private val onRoll = jda.onCommand("roll", timeout = 2.seconds) { event ->
        val formula = event.getOption<String>("formula") ?: ""

        doRoll(formula, event.user.effectiveName, jda) {
            event.reply(it).queue()
        }
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            onRoll.cancel()
            return null
        } catch (exception: Exception) {
            log.error("tryClose - failed to close", exception)
            return ThrowableError(exception)
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val message = event.message.contentStripped
        if (!message.lowercase().startsWith("%roll")) return

        val messageTokens = message.split(" ")
        if (messageTokens.size < 2) return

        val formula = messageTokens.subList(1, messageTokens.size).joinToString(" ")

        doRoll(formula, event.author.effectiveName, event.jda) {
            event.message.reply(it).queue()
        }

    }

    private fun doRoll(formula: String, userName: String, jda: JDA, sendReply: (String) -> Unit) {
        val parsed = parse(formula)

        try {
            var rawReply = parsed.prettyPrint()
            diceMap.mapValues {
                val diceEmojiId = it.value
                val default = "[${it.key}]"
                try {
                    val diceEmoji = jda.getEmojiById(diceEmojiId)
                    diceEmoji?.asMention ?: default
                } catch (e: Exception) {
                    log.error("Failed to get emoji for {} / {}", it.key, it.value, e)
                    default
                }
            }.forEach {
                rawReply = rawReply.replace("[${it.key}]", it.value)
            }
            sendReply(rawReply)
        } catch (error: Exception) {
            sendReply("$userName Error rolling $formula => ${error.message}.")
        }
    }

    private fun ExpressionResult.prettyPrint(): String {
        return when (this.expression) {
            is NumberDiceExpression -> {

                val result =
                    when ((this.expression as NumberDiceExpression).modifier) {
                        is TargetModifier -> this.resultList.count { it.face.enabled }
                        else -> this.resultList.sumEnabled()
                    }

                " " + this.resultList.map {
                    it.prettyPrint()
                }.reduce { acc, s ->
                    "$acc $s"
                } + " = " + result
            }

            is AddOperatorExpression -> {
                this.subResults.map { "(${it.prettyPrint()})" }
                    .reduce { acc, s -> "$acc + $s" } + " = ${this.resultList.sumEnabled()}"
            }

            is SubOperatorExpression -> {
                this.subResults.map { "(${it.prettyPrint()})" }.reduce { acc, s -> "$acc - $s" } + " = ${
                    this.resultList.map { it.face.getValue() }.reduce { acc, i -> acc - i }
                }"
            }

            else -> {
                this.resultList.map { it.face.getValue().toString() }.reduce { acc, s -> "$acc, $s" }
            }
        }
    }

    private fun List<RollResult>.sumEnabled() = this.sumOf {
        when (it) {
            is GroupRollResult -> if (it.face.enabled) it.face.getValue()*it.count else 0
            else -> if (it.face.enabled) it.face.getValue() else 0
        }

    }

    private fun RollResult.prettyPrint(): String {
        return when (this) {
            is GroupRollResult -> this.prettyPrint()
            else -> {
                val diceTag = if (this.diceType != null) "[d${this.diceType!!.getName()}]" else ""
                val final = "$diceTag ${this.face.getSymbol()}"
                return if (this.face.enabled) final else "~~$final~~"
            }
        }
    }

    private fun GroupRollResult.prettyPrint(): String {
        val diceTag = if (this.diceType != null) "[d${this.diceType!!.getName()}]" else ""
        val final = "$diceTag ${this.face.getSymbol()} = ${this.count}\n"
        return if (this.face.enabled) final else "~~$final~~"
    }
}