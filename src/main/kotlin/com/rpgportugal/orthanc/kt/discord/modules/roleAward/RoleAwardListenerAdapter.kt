package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Logging
import com.rpgportugal.orthanc.kt.logging.log
import net.dv8tion.jda.api.JDA

class RoleAwardListenerAdapter(
    val jda: JDA,
    val roleId: Long,
    val adminAwardRole: Long,
    val threshold: Long,
    val warningChannelId: Long,
    val emojiNames: List<String>,
) : CloseableListenerAdapter(), Logging {

    init {
        jda.addEventListener(this)
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        } catch (e: Exception) {
            log.error("tryClose - failed to close listener adapter", e)
            return ThrowableError(e)
        }
    }

}