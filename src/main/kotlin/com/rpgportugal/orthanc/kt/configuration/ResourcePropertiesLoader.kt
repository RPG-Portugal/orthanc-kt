package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.error.NullInputStreamError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ResourcePropertiesLoader : PropertiesLoader {

    companion object {
        @JvmStatic
        val LOG: Logger = LoggerFactory.getLogger(ResourcePropertiesLoader::class.java)
    }

    override fun load(fileName: String): Either<PropertiesLoadError, Properties> =
        Either.catch {
            ResourcePropertiesLoader::class.java.classLoader.getResourceAsStream(fileName)
        }.mapLeft {
            LOG.error("Error while loading $fileName", it)
            ThrowableError(it)
        }.flatMap { stream ->
            if (stream != null) {
                stream.use {
                    val p = Properties()
                    p.load(it)
                    Either.Right(p)
                }
            } else {
                LOG.error("Error while loading $fileName")
                Either.Left(NullInputStreamError(fileName, "File not found"))
            }
        }
}