package com.rpgportugal.orthanc.kt.discord.application.manager

import com.rpgportugal.orthanc.kt.error.DomainError

interface ApplicationManager {
    fun start(moduleName: String): DomainError?
    fun stop(moduleName: String): DomainError?
    fun failIfNotRunning(moduleName: String): DomainError?
}