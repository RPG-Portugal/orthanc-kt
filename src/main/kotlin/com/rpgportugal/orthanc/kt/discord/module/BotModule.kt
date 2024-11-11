package com.rpgportugal.orthanc.kt.discord.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ApplicationManager
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.util.TryCloseable
import org.koin.core.component.KoinComponent

interface BotModule : Loggable, KoinComponent {
    fun getName(): String
    fun start(applicationManager: ApplicationManager): Either<DomainError, TryCloseable>
}