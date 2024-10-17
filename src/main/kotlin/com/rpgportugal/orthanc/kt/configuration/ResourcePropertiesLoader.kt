package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.error.*
import java.util.*

class ResourcePropertiesLoader : PropertiesLoader {
    private val classLoader: ClassLoader = Properties::class.java.classLoader

    override fun load(fileName: String): Either<PropertiesLoadError, Properties> =
        Either.catch {
            classLoader.getResourceAsStream(fileName)
        }.mapLeft(::ThrowableError).flatMap {
            it.use { stream ->
                stream?.let {
                    val p = Properties()
                    p.load(stream)
                    Either.Right(p)
                } ?: Either.Left(NullInputStream(fileName, "Input Stream is null"))
        }
    }
}