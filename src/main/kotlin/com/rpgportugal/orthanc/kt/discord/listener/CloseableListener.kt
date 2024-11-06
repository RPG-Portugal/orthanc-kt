package com.rpgportugal.orthanc.kt.discord.listener

import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter

abstract class CloseableListener(private val jda: JDA): ListenerAdapter(), TryCloseable {
    final override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return tryCloseListener()
        } catch (exception: Exception) {
            return ThrowableError(exception)
        }
    }

    abstract fun tryCloseListener(): DomainError?
}