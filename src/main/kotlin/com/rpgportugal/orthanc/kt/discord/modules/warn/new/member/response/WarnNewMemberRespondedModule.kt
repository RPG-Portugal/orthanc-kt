package com.rpgportugal.orthanc.kt.discord.modules.warn.new.member.response

import arrow.core.Either
import arrow.core.flatMap
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.BotModuleError
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.persistence.repository.module.BotModuleConfigurationRepository
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toLeft
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toRight
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA

class WarnNewMemberRespondedModule(
    private val jda: JDA,
    private val botModuleConfigurationRepository: BotModuleConfigurationRepository,
) : BotModule {

    override fun getName(): String = "warn-new-members"

    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {
        return botModuleConfigurationRepository.getWarnNewMemberRespondedConfiguration().flatMap {
            val arrivalChannel = jda.getTextChannelById(it.arrivalChannelId)
                ?: return BotModuleError.InvalidConfiguration("arrivalChannelId ${it.arrivalChannelId} is invalid")
                    .toLeft()

            val warnChannel = jda.getTextChannelById(it.warningChannelId)
                ?: return BotModuleError.InvalidConfiguration("warningChannelId ${it.warningChannelId} is invalid")
                    .toLeft()

            val moderatorRole = jda.getRoleById(it.moderatorRoleId)
                ?: return BotModuleError.InvalidConfiguration("moderatorRoleId ${it.moderatorRoleId} is invalid")
                    .toLeft()

            TryCloseable {
                WarnNewMemberResponseListener(jda, arrivalChannel, warnChannel, moderatorRole).tryClose()
            }.toRight()
        }
    }
}