package com.rpgportugal.orthanc.kt.error

interface ModuleStateError : DomainError {
    data class ModuleNotRunning(
        val moduleName: String,
        override val message: String = "module $moduleName is not running",
    ) : ModuleStateError

    data class ModuleDoesNotExist(
        val moduleName: String,
        override val message: String = "module $moduleName does not exist",
    ) : ModuleStateError
}