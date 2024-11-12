package com.rpgportugal.orthanc.kt.discord.application.manager.impl

import arrow.core.Either
import arrow.core.mapNotNull
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ModuleStateError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.util.TryCloseable

class ModuleStateManagerImpl(
    private val botModules: Map<String, BotModule>,
) : ModuleStateManager, Loggable {

    private val stateManager = StateManager(this, botModules)

    override fun start(moduleName: String): DomainError? {
        val module = botModules[moduleName] ?: run {
            log.error("start - No such module: {}", moduleName)
            return ModuleStateError.ModuleDoesNotExist(moduleName)
        }

        return stateManager.accessState { runningMods ->
            stopInternal(moduleName, runningMods)?.let { error ->
                log.error("start - Failed to stop module: {}", error.message)
                return@accessState error
            }

            when (val result = module.start(this)) {
                is Either.Left -> {
                    val error = result.value
                    log.error("start - module {} failed to start: {}", moduleName, error.message)
                    error
                }

                is Either.Right -> {
                    log.info("start - module {} is running", moduleName)
                    runningMods[moduleName] = result.value
                    null
                }
            }
        }
    }

    override fun stop(moduleName: String): DomainError? {
        return stateManager.accessState { runningMods ->
            stopInternal(moduleName, runningMods)
        }
    }

    override fun failIfNotRunning(moduleName: String): DomainError? {
        if (!botModules.containsKey(moduleName)) {
            log.error("failIfNotRunning - module does not exist: {}", moduleName)
            return ModuleStateError.ModuleDoesNotExist(moduleName)
        }
        return stateManager.accessState { runningMods ->
            if (!runningMods.contains(moduleName)) {
                log.error("failIfNotRunning - module {} is not running", moduleName)
                ModuleStateError.ModuleNotRunning(moduleName)
            } else {
                null
            }
        }
    }

    private fun stopInternal(moduleName: String, runningMods: MutableMap<String, TryCloseable>): DomainError? {
        if (!runningMods.containsKey(moduleName)) {
            log.error("stop - module {} is not running", moduleName)
            return ModuleStateError.ModuleNotRunning(moduleName)
        }
        return when (val result = runningMods.remove(moduleName)) {
            is TryCloseable -> result.tryClose()?.also {
                log.error("stop - module {} failed to stop: {}", moduleName, it.message)
            }
            else -> {
                log.info("stop - Module {} not found", moduleName)
                null
            }
        }
    }

    private class StateManager(private val moduleStateManager: ModuleStateManager, allModules: Map<String, BotModule>) :
        Loggable {
        private val runningModules = allModules.mapNotNull {
            when (val result = it.value.start(moduleStateManager)) {
                is Either.Right -> {
                    log.info("StateManager - module {} started with success", it.key)
                    result.value
                }

                is Either.Left -> {
                    log.error("StateManager - failed to initialize module {} - {}", it.key, result.value.message)
                    null
                }
            }
        }.toMutableMap()

        companion object {
            @JvmStatic
            private val LOCK: Any = Any()
        }

        fun <T> accessState(func: (MutableMap<String, TryCloseable>) -> T): T {
            synchronized(LOCK) {
                return func(runningModules)
            }
        }
    }

    override fun toString(): String {
        return "ApplicationManagerImpl(" +
                "botModules=${botModules.keys.joinToString(",")}, " +
                "runningModules=${stateManager.accessState { runningMods -> runningMods.keys.joinToString(",") }})"
    }
}