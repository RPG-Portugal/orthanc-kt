package com.rpgportugal.orthanc.kt.error


// Database Errors
sealed interface DiceModuleError : DomainError {
    data class DiceParsingError(
        val formula: String,
        override val message: String,
    ) : DiceModuleError
}


