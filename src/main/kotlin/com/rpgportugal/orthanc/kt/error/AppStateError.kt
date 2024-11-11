package com.rpgportugal.orthanc.kt.error

interface AppStateError : DomainError {
    data class ModuleNotRunning(
        val moduleName: String,
        override val message: String = "module $moduleName is not running"
    ) : AppStateError
}