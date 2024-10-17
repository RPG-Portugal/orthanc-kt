package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.error.NullInputStreamError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import java.util.*

class ResourcePropertiesLoader : PropertiesLoader {

    override fun load(fileName: String): Either<PropertiesLoadError, Properties> =
        Either.catch {
            Properties::class.java.classLoader
                .getResourceAsStream(fileName)
        }.mapLeft(::ThrowableError).flatMap {
            it?.use { stream ->
                val p = Properties()
                p.load(stream)
                Either.Right(p)
            } ?: Either.Left(NullInputStreamError(fileName, "File not found"))
        }
}