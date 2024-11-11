package com.rpgportugal.orthanc.kt.persistence.sql.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.SpamCatcherConfiguration
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.persistence.sql.util.QueryUtil
import jakarta.persistence.EntityManager
import jakarta.persistence.NoResultException
import jakarta.persistence.NonUniqueResultException
import jakarta.persistence.TypedQuery


class SqlBotModuleConfigurationRepository(
    private val entityManager: EntityManager
) : BotModuleConfigurationRepository, Loggable{

    override fun getRoleAwardConfiguration(): Either<DbError, RoleAwardConfiguration> {
        val query =
            "select RA from RoleAwardConfiguration RA " +
                    "join fetch Emoji E " +
                    "join fetch JobConfiguration JC on RA.jobConfiguration.id = JC.id " +
                    "where RA.id = 1";

        val cls = RoleAwardConfiguration::class.java

        val typedQuery =
            entityManager.createQuery(query, cls)

        return QueryUtil.getSingleIdValue(typedQuery, cls,1L)
    }

    override fun getSpamCatcherConfiguration(): Either<DbError, SpamCatcherConfiguration> {
        val query =
            "select SCC from SpamCatcherConfiguration SCC " +
                    "join fetch Emoji E " +
                    "join fetch JobConfiguration " +
                    "where SCC.id = 1";

        val cls = SpamCatcherConfiguration::class.java
        val typedQuery = entityManager.createQuery(query, cls)

        return QueryUtil.getSingleIdValue(typedQuery, cls)
    }


}