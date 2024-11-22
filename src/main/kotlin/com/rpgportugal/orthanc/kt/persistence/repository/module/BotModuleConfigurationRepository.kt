package com.rpgportugal.orthanc.kt.persistence.repository.module

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.dto.module.RoleAwardConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.SpamCatcherConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.ThreadUpdateConfiguration
import com.rpgportugal.orthanc.kt.persistence.dto.module.WarnNewMemberRespondedConfiguration

interface BotModuleConfigurationRepository {
    fun getRoleAwardConfiguration(): Either<DbError, RoleAwardConfiguration>
    fun getSpamCatcherConfiguration(): Either<DbError, SpamCatcherConfiguration>
    fun getThreadUpdateConfiguration(): Either<DbError, ThreadUpdateConfiguration>
    fun getWarnNewMemberRespondedConfiguration(): Either<DbError, WarnNewMemberRespondedConfiguration>
}