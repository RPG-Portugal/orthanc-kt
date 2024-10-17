package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.configuration.ResourcePropertiesLoader
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module


object PropertiesModule : DepModule {
    override val module = module {
        singleOf(::ResourcePropertiesLoader) bind PropertiesLoader::class
    }
}