package com.rpgportugal.orthanc.kt.dependencies

import arrow.core.Either
import com.rpgportugal.orthanc.kt.configuration.PropertiesLoader
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
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
import jakarta.persistence.EntityManager
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.community.dialect.SQLiteDialect
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import org.koin.dsl.bind
import org.koin.dsl.module

object DbModule : DepModule, Logging {

    override val module = module {
        single {
            val properties = when (val either = get<PropertiesLoader>().load("app.properties")) {
                is Either.Right -> either.value
                is Either.Left -> {
                    log.error("Failed to load app.properties: {}", either.value.message)
                    throw Exception("Failed to load app.properties")
                }
            }

            properties.run {
                setProperty(
                    "hibernate.connection.provider_class",
                    HikariCPConnectionProvider::class.java.name
                )
                setProperty(
                    "hibernate.hbm2ddl.auto",
                    "update"
                )
                if (getProperty("is.sqlite", "false").toBoolean()) {
                    setProperty(
                        "hibernate.dialect",
                        SQLiteDialect::class.java.name
                    )
                }
            }

            Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Application::class.java)
                .addAnnotatedClass(Emoji::class.java)
                .addAnnotatedClass(JobConfiguration::class.java)
                .addAnnotatedClass(RoleAwardConfiguration::class.java)
                .buildSessionFactory()
                ?: throw Exception("Unable to build session factory")

        } bind SessionFactory::class
        factory { get<SessionFactory>().createEntityManager() } bind EntityManager::class
        factory { SqlApplicationRepository(get()) } bind ApplicationRepository::class
        factory { SqlEmojiRepository(get()) } bind EmojiRepository::class
        factory { SqlJobRepository(get()) } bind JobRepository::class
    }
}