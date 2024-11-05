package com.rpgportugal.orthanc.kt.discord.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.error.MissingPropertyError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import net.dv8tion.jda.api.JDA
import org.koin.core.component.KoinComponent
import java.util.*

interface BotModule : Logging, KoinComponent {
    val propertiesLoader: PropertiesLoader
    val propertiesEither: Either<PropertiesLoadError, Properties>

    fun getName(): String

    fun isEnabled(): Boolean {
        return when (propertiesEither) {
            is Either.Left -> false
            is Either.Right -> {
                when (val enabledProperty = Either.catch {
                    (propertiesEither as Either.Right<Properties>).value.getProperty("enabled")
                        ?: throw Exception("Missing enabled property!")
                }.mapLeft {
                    MissingPropertyError("enabled", it.message ?: "")
                }) {
                    is Either.Left -> {
                        log.error("Missing property ${enabledProperty.value.propertyName}.")
                        false
                    }

                    is Either.Right -> enabledProperty.value == "true"
                }
            }
        }
    }

    fun attach(jda: JDA)

    fun detach(jda: JDA)
}