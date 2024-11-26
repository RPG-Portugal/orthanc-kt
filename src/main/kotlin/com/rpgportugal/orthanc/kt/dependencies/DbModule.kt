package com.rpgportugal.orthanc.kt.dependencies

import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.application.ApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.persistence.repository.job.JobRepository
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.module.ModuleStateManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.repository.permission.RolePermissionRepository
import com.rpgportugal.orthanc.kt.persistence.sql.application.SqlApplicationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.emoji.SqlEmojiRepository
import com.rpgportugal.orthanc.kt.persistence.sql.job.SqlJobRepository
import com.rpgportugal.orthanc.kt.persistence.sql.module.SqlBotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.module.SqlModuleStateManagementConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.permission.SqlRolePermissionRepository
import jakarta.persistence.Entity
import jakarta.persistence.EntityManager
import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.reflections.Reflections
import java.util.*


object DbModule : DepModule, Loggable {

    override val module = module {
        singleOf(::buildSessionFactory).bind(SessionFactory::class)
        factoryOf(::createEntityManager).bind(EntityManager::class)
        factoryOf(::SqlApplicationRepository).bind(ApplicationRepository::class)
        factoryOf(::SqlEmojiRepository).bind(EmojiRepository::class)
        factoryOf(::SqlJobRepository).bind(JobRepository::class)
        factoryOf(::SqlBotModuleConfigurationRepository).bind(BotModuleConfigurationRepository::class)
        factoryOf(::SqlModuleStateManagementConfigurationRepository).bind(ModuleStateManagementConfigurationRepository::class)
        factoryOf(::SqlRolePermissionRepository).bind(RolePermissionRepository::class)
    }

    private fun createEntityManager(sessionFactory: SessionFactory): EntityManager {
        return sessionFactory.createEntityManager()
    }

    private fun buildSessionFactory(): SessionFactory {
        val jdbcUrl = System.getenv("DATABASE_URL") ?: let {
            log.error("environment variable DATABASE_URL is not set")
            throw Exception("environment variable DATABASE_URL is not set")
        }

        val properties = Properties().apply {
            setProperty(
                "hibernate.connection.provider_class",
                HikariCPConnectionProvider::class.java.name
            )
            setProperty(
                "hibernate.hbm2ddl.auto",
                "update"
            )
            setProperty(
                "hibernate.connection.url",
                jdbcUrl
            )
        }

        val persistenceClasses =
            Reflections("com.rpgportugal.orthanc.kt.persistence.dto")
                .getTypesAnnotatedWith(Entity::class.java) ?: emptyList()

        return Configuration()
            .addProperties(properties)
            .let {
                var cfg = it
                for (cls in persistenceClasses) {
                    cfg = cfg.addAnnotatedClass(cls)
                }
                cfg
            }
            .buildSessionFactory()
            ?: throw Exception("Unable to build session factory")

    }
}