package com.rpgportugal.orthanc.kt.discord.modules.dice

import com.rpgportugal.orthanc.kt.error.DomainError


// Database Errors
sealed interface DiceModuleError : DomainError

data class DiceParsingError(
    val formula: String,
    override val message: String
) : DiceModuleError
