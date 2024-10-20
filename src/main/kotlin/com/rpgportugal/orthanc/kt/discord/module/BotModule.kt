package com.rpgportugal.orthanc.kt.discord.module

import com.rpgportugal.orthanc.kt.logging.Logging
import net.dv8tion.jda.api.JDA

interface BotModule : Logging {

    fun getName(): String

    fun isEnabled():Boolean

    fun attach(jda: JDA)

    fun detach(jda: JDA)
}