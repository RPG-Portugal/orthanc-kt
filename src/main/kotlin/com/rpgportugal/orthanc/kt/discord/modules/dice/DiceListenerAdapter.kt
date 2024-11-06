package com.rpgportugal.orthanc.kt.discord.modules.dice

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DiceModuleError
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Logging
import dev.diceroll.parser.*
import dev.minn.jda.ktx.events.onCommand
import dev.minn.jda.ktx.interactions.components.getOption
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import kotlin.time.Duration.Companion.seconds

class DiceListenerAdapter(
    private val jda: JDA,
    private val diceMap: Map<String, String>
) : CloseableListenerAdapter(), Logging {

    private val onRoll = jda.onCommand("roll", timeout = 2.seconds) { event ->
        val formula = event.getOption<String>("formula") ?: ""

        doRoll(formula, event.user.effectiveName, jda) {
            event.reply(it).queue()
        }
    }

    override fun tryClose(): DomainError? {
        onRoll.cancel()
        return null
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

    fun ResultTree.prettyPrint(): String {
        return when (this.expression) {
            is NDice -> {
                val nDice = this.expression as NDice
                if (nDice.numberOfDice > 1) {
                    this.results.joinToString(", ") { it.prettyPrint() } + " = ${this.value}"
                } else {
                    "[d${nDice.numberOfFaces}] ${this.value}"
                }
            }

            is KeepDice -> prettyPrintKeepHighDice(this.expression as KeepDice, this)
            is KeepLowDice -> prettyPrintKeepLowDice(this.expression as KeepLowDice, this)
            is TargetPoolDice -> {
                val targetPoolExpr = this.expression as TargetPoolDice
                this.results.joinToString(", ") {
                    when (targetPoolExpr.comparison) {
                        Comparison.GREATER_THAN -> if (it.value >= targetPoolExpr.target) {
                            "**${it.prettyPrint()}**"
                        } else {
                            it.prettyPrint()
                        }

                        Comparison.LESS_THAN -> if (it.value <= targetPoolExpr.target) {
                            "**${it.prettyPrint()}**"
                        } else {
                            it.prettyPrint()
                        }

                        Comparison.EQUAL_TO -> if (it.value == targetPoolExpr.target) {
                            "**${it.prettyPrint()}**"
                        } else {
                            it.prettyPrint()
                        }
                    }
                } + " = ${this.value}"
            }

            is FudgeDice -> {
                val fudgeExpr = this.expression as FudgeDice
                fudgeExpr.numberOfFaces
                if (this.results.isNotEmpty())
                    this.results.joinToString(", ") { it.prettyPrint() } + " = ${this.value}"
                else
                    "[dF] ${this.value}"
            }

            is MathExpression -> {
                val mathExpr = this.expression as MathExpression

                "(${this.results[0].prettyPrint()}) ${mathExpr.operation.description} (${this.results[1].prettyPrint()}) = ${this.value}"
            }

            else -> {
                if (this.results.isNotEmpty())
                    this.results.joinToString(", ") { it.prettyPrint() } + " = ${this.value}"
                else
                    "${this.value}"
            }
        }
    }

    private fun prettyPrintKeepHighDice(kDice: KeepDice, resultTree: ResultTree): String {
        val sortedResults = resultTree.results.sortedBy { it.value }
        return prettyPrintKeepDice(kDice.numberToKeep, resultTree, sortedResults)
    }

    private fun prettyPrintKeepLowDice(kDice: KeepLowDice, resultTree: ResultTree): String {
        val sortedResults = resultTree.results.sortedBy { it.value }.reversed()
        return prettyPrintKeepDice(kDice.numberToKeep, resultTree, sortedResults)
    }

    private fun prettyPrintKeepDice(numberToKeep: Int, resultTree: ResultTree, sortedResults: List<ResultTree>): String {
        val survivors = sortedResults.takeLast(numberToKeep)
        val failures = sortedResults.filter { !survivors.contains(it) }

        return resultTree.results.joinToString(", ") {
            if (failures.contains(it)) {
                "~~"
            } else {
                ""
            } +
                    it.prettyPrint() +
                    if (failures.contains(it)) {
                        "~~"
                    } else {
                        ""
                    }
        } + " = ${resultTree.value}"
    }
}