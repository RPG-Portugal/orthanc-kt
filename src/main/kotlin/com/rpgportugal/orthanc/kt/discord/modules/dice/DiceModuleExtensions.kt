package com.rpgportugal.orthanc.kt.discord.modules.dice

import dev.diceroll.parser.*

fun ResultTree.prettyPrint(): String {
    return when (this.expression) {
        is NDice -> {
            val nDice = this.expression as NDice
            if (nDice.numberOfDice > 1) {
                this.results.map { it.prettyPrint() }.joinToString(", ") + " = ${this.value}"
            } else {
                "[d${nDice.numberOfFaces}] ${this.value}"
            }
        }

        is KeepDice -> prettyPrintKeepHighDice(this.expression as KeepDice, this)
        is KeepLowDice -> prettyPrintKeepLowDice(this.expression as KeepLowDice, this)
        is TargetPoolDice -> {
            val targetPoolExpr = this.expression as TargetPoolDice
            this.results.map {
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
            }.joinToString(", ") + " = ${this.value}"
        }

        is FudgeDice -> {
            val fudgeExpr = this.expression as FudgeDice
            fudgeExpr.numberOfFaces
            if (this.results.isNotEmpty())
                this.results.map { it.prettyPrint() }.joinToString(", ") + " = ${this.value}"
            else
                "[dF] ${this.value}"
        }

        is MathExpression -> {
            val mathExpr = this.expression as MathExpression

            "(${this.results[0].prettyPrint()}) ${mathExpr.operation.description} (${this.results[1].prettyPrint()}) = ${this.value}"
        }

        else -> {
            if (this.results.isNotEmpty())
                this.results.map { it.prettyPrint() }.joinToString(", ") + " = ${this.value}"
            else
                "${this.value}"
        }
    }
}

fun prettyPrintKeepHighDice(kDice: KeepDice, resultTree: ResultTree): String {
    val sortedResults = resultTree.results.sortedBy { it.value }
    return prettyPrintKeepDice(kDice.numberToKeep, resultTree, sortedResults)
}

fun prettyPrintKeepLowDice(kDice: KeepLowDice, resultTree: ResultTree): String {
    val sortedResults = resultTree.results.sortedBy { it.value }.reversed()
    return prettyPrintKeepDice(kDice.numberToKeep, resultTree, sortedResults)
}

fun prettyPrintKeepDice(numberToKeep: Int, resultTree: ResultTree, sortedResults: List<ResultTree>): String {
    val survivors = sortedResults.takeLast(numberToKeep)
    val failures = sortedResults.filter { !survivors.contains(it) }

    return resultTree.results.map {
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
    }.joinToString(", ") + " = ${resultTree.value}"
}