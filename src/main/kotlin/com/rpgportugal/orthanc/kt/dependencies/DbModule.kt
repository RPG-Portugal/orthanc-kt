package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.persistence.dto.app.Application
import com.rpgportugal.orthanc.kt.persistence.dto.emoji.Emoji
import com.rpgportugal.orthanc.kt.persistence.dto.job.JobConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.job.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.application.db.SqlApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.db.SqlEmojiRepository
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import com.rpgportugal.orthanc.kt.persistence.repository.job.db.SqlJobRepository
import com.rpgportugal.orthanc.kt.persistence.repository.jpa.OrthancPersistenceUnitInfo
import jakarta.persistence.EntityManager
import org.hibernate.jpa.HibernatePersistenceProvider
import org.koin.dsl.bind
import org.koin.dsl.module

object DbModule : DepModule, Logging {

    private val classes = listOf(
        Application::class.java,
        Emoji::class.java,
        JobConfiguration::class.java,
        RoleAwardConfiguration::class.java,
    )

    private val provider =
        HibernatePersistenceProvider()
            .createContainerEntityManagerFactory(
                OrthancPersistenceUnitInfo(classes, ""),
                emptyMap<Any, Any>()
            )

    override val module = module {
        factory { provider.createEntityManager() } bind EntityManager::class
        factory { SqlApplicationRepository(get()) } bind ApplicationRepository::class
        factory { SqlEmojiRepository(get()) } bind EmojiRepository::class
        factory { SqlJobRepository(get()) } bind JobRepository::class
    }
}