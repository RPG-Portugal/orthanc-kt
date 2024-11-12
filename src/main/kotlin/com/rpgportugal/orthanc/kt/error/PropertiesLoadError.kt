package com.rpgportugal.orthanc.kt.error

sealed interface PropertiesLoadError : DomainError {
    data class NullInputStreamError(
        val fileName: String,
        override val message: String,
    ) : PropertiesLoadError
}