package com.rpgportugal.orthanc.kt.persistence.sql.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.persistence.dto.module.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.SpamCatcherConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.ThreadUpdateConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil.getSingleIdValue
import jakarta.persistence.EntityManager


class SqlBotModuleConfigurationRepository(
    private val entityManager: EntityManager,
) : BotModuleConfigurationRepository, Loggable {

    override fun getRoleAwardConfiguration(): Either<DbError, RoleAwardConfiguration> {
        val query =
            "select RA from RoleAwardConfiguration RA " +
                    "join fetch JobConfiguration JC on RA.jobConfiguration.id = JC.id " +
                    "join fetch Emoji EM on RA.emoji.key = EM.key " +
                    "where RA.id = 1 "

        return entityManager
            .createQuery(query, RoleAwardConfiguration::class.java)
            .getSingleIdValue(1L)
    }

    override fun getSpamCatcherConfiguration(): Either<DbError, SpamCatcherConfiguration> {
        val query =
            "select SCC from SpamCatcherConfiguration SCC " +
                    "join fetch JobConfiguration JC on SCC.jobConfiguration.id = JC.id " +
                    "where SCC.id = 1"

        return entityManager
            .createQuery(query, SpamCatcherConfiguration::class.java)
            .getSingleIdValue(1L)
    }

    override fun getThreadUpdateConfiguration(): Either<DbError, ThreadUpdateConfiguration> {
        val query =
            "select TUC from ThreadUpdateConfiguration TUC where TUC.id = 1"

        return entityManager
            .createQuery(query, ThreadUpdateConfiguration::class.java)
            .getSingleIdValue(1L)
    }


}