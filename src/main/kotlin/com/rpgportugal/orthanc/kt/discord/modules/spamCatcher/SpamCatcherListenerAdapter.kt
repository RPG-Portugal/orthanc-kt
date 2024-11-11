package com.rpgportugal.orthanc.kt.discord.modules.spamCatcher

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import net.dv8tion.jda.api.JDA

class SpamCatcherListenerAdapter(
    private val jda: JDA
) : CloseableListenerAdapter() {
    init {
        jda.addEventListener(this)
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        } catch (e: Exception) {
            return ThrowableError(e)
        }
    }
}