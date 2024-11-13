package com.rpgportugal.orthanc.kt.discord.modules.management

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.discord.permission.Permission
import com.rpgportugal.orthanc.kt.discord.permission.PermissionManager
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.dto.module.ModuleStateManagementConfiguration
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.UserSnowflake
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ModuleStateManagementListenerAdapter(
    private val jda: JDA,
    private val moduleStateManager: ModuleStateManager,
    private val permissionManager: PermissionManager,
    private val moduleStateManagementConfiguration: ModuleStateManagementConfiguration,
    private val evokerModuleName: String,
) : CloseableListenerAdapter(), Loggable {

    private enum class AppManagementOperation {
        Start,
        Stop,
        Check,
    }

    init {
        jda.addEventListener(this)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val msg = event.message
        val cmd = msg.contentStripped.lowercase().split(' ')
        if (cmd.size != 2) {
            return
        }

        val command = cmd[0]
        val moduleName = cmd[1]

        if (moduleName == this.evokerModuleName) {
            log.warn("cannot evoke state commands on self: {}", moduleName)
            return
        }

        val (error, operation) = when (command) {
            "\$start" -> moduleStateManager.start(moduleName) to AppManagementOperation.Start
            "\$stop" -> moduleStateManager.stop(moduleName) to AppManagementOperation.Stop
            "\$check" -> moduleStateManager.failIfNotRunning(moduleName) to AppManagementOperation.Check
            else -> return
        }

        val member =
            msg.guild
                .getMember(UserSnowflake.fromId(msg.author.idLong))
                ?: return

        when (val res = permissionManager.hasPermission(Permission.ManageModuleState, member)) {
            is Either.Right -> {
                if (!res.value) {
                    log.error("onMessageReceived - {} does not have permission to do this operation", member.asMention)
                    return
                }
            }

            is Either.Left -> {
                log.error("onMessageReceived - failed to retrieve permissions - {}", res.value.message)
                throw Exception(res.value.message)
            }
        }

        val text =
            when (operation) {
                AppManagementOperation.Start, AppManagementOperation.Stop -> {
                    if (error != null) {
                        log.error("onMessageReceived - Management operation failed - {}", error.message)
                        "${member.asMention} failed to $operation $moduleName"
                    } else {
                        log.warn(
                            "onMessageReceived - Management operation - {} {} {}",
                            member.nickname,
                            operation,
                            member
                        )
                        "${member.asMention} was able to $operation $moduleName"
                    }
                }

                AppManagementOperation.Check -> {
                    val state = if (error != null) {
                        "Stopped"
                    } else {
                        "Running"
                    }
                    "${member.asMention} module $moduleName is $state"
                }
            }

        jda.getTextChannelById(moduleStateManagementConfiguration.warningChannelId)?.sendMessage(text)?.queue()
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        } catch (exception: Exception) {
            log.error("Failed to close AppManagementListenerAdapter", exception)
            return ThrowableError(exception)
        }
    }

}