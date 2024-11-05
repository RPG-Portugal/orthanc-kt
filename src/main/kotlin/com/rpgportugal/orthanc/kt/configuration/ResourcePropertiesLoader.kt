package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.error.NullInputStreamError
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ResourcePropertiesLoader(private val classLoader: ClassLoader) : PropertiesLoader {

    companion object {
        @JvmStatic
        val LOG: Logger = LoggerFactory.getLogger(ResourcePropertiesLoader::class.java)

        @JvmStatic
        val PROPERTIES_BASE: String = getPropertiesBasePath()

        @JvmStatic
        private fun getPropertiesBasePath(): String {
            val result =
                ResourcePropertiesLoader(ClassLoader.getSystemClassLoader())
                    .loadFromBase("", "env.properties")

            return when (result) {
                is Either.Right -> result.value.getProperty("env","")
                is Either.Left -> throw Exception(result.value.message)
            }
        }
    }

    fun loadFromBase(fileName: String, base: String = PROPERTIES_BASE): Either<PropertiesLoadError, Properties> {
            val path = "$base/$fileName";
            return Either.catch {
                classLoader.getResourceAsStream(path)
            }.mapLeft {
                LOG.error("Error while loading $path", it)
                ThrowableError(it)
            }.flatMap { stream ->
                if (stream != null) {
                    stream.use {
                        val p = Properties()
                        p.load(it)
                        Either.Right(p)
                    }
                } else {
                    LOG.error("Error while loading $path")
                    Either.Left(NullInputStreamError(path, "File not found"))
                }
            }
        }

    override fun load(fileName: String): Either<PropertiesLoadError, Properties> =
       loadFromBase(fileName=fileName)
}