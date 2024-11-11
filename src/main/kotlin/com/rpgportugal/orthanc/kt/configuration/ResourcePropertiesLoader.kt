package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import java.util.*

class ResourcePropertiesLoader(private val classLoader: ClassLoader) : PropertiesLoader {

    companion object : Loggable

    private fun loadFromBase(fileName: String, base: String = ""): Either<PropertiesLoadError, Properties> {

        val path =
            if (base.isEmpty()) fileName else "$base/$fileName"

        return Either.catch {
            classLoader.getResourceAsStream(path)
        }.mapLeft {
            log.error("Error while loading $path", it)
            ThrowableError(it)
        }.flatMap { stream ->
            if (stream != null) {
                stream.use {
                    val p = Properties()
                    p.load(it)
                    Either.Right(p)
                }
            } else {
                log.error("Error while loading $path")
                Either.Left(PropertiesLoadError.NullInputStreamError(path, "File not found"))
            }
        }
    }

    override fun load(fileName: String): Either<PropertiesLoadError, Properties> =
        loadFromBase(fileName)
}