package com.rpgportugal.orthanc.kt.error

interface BotModuleError : DomainError {
    data class InvalidConfiguration(override val message: String) : BotModuleError

}