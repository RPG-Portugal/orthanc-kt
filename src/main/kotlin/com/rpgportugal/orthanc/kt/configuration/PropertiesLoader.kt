package com.rpgportugal.orthanc.kt.configuration

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.PropertiesLoadError
import java.util.*

interface PropertiesLoader {
    fun load(fileName: String): Either<PropertiesLoadError, Properties>
}