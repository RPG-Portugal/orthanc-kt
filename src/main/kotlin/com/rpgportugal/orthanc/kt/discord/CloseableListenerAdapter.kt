package com.rpgportugal.orthanc.kt.discord

import com.rpgportugal.orthanc.kt.util.TryCloseable
import net.dv8tion.jda.api.hooks.ListenerAdapter

abstract class CloseableListenerAdapter : ListenerAdapter(), TryCloseable