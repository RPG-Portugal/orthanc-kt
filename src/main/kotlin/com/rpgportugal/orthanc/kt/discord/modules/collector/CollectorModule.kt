package com.rpgportugal.orthanc.kt.discord.modules.collector

import arrow.core.Either
import arrow.core.none
import com.rpgportugal.orthanc.kt.discord.application.manager.ModuleStateManager
import com.rpgportugal.orthanc.kt.discord.domain.emoji.EmojiCategory
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.dice.DiceListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.BoosterTypeRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CadernetaRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.UserCollectorProfileRepository
import com.rpgportugal.orthanc.kt.persistence.repository.emoji.EmojiRepository
import com.rpgportugal.orthanc.kt.util.TryCloseable
import dev.minn.jda.ktx.interactions.commands.option
import dev.minn.jda.ktx.interactions.commands.slash
import dev.minn.jda.ktx.interactions.commands.updateCommands
import net.dv8tion.jda.api.JDA

class CollectorModule(
    private val jda: JDA,
    private val collectableRepository: CollectableRepository,
    private val boosterTypeRepository: BoosterTypeRepository,
    private val userCollectorProfileRepository: UserCollectorProfileRepository,
    private val collectableItemRepository: CollectableItemRepository,
    private val cadernetaRepository: CadernetaRepository,
) : BotModule, Loggable {

    override fun getName(): String = "collector"

    override fun start(moduleStateManager: ModuleStateManager): Either<DomainError, TryCloseable> {
        try {

            val nrCollectables = collectableRepository.findAll().getOrNull()?.count() ?: 0
            val boosterTypes = boosterTypeRepository.findAll().getOrNull()
            println("CollectorModule started!!!!!!!!!!! >>> $nrCollectables collectables found in the database and ${boosterTypes?.count() ?: 0} booster types")

            val boosterListener = BoosterPullListenerAdapter(jda,
                boosterTypeRepository,
                userCollectorProfileRepository,
                collectableItemRepository
            )

            val inventoryListener = InventoryListenerAdapter(jda,
                collectableItemRepository
            )

            val cadernetaListenerAdapter = CadernetaListenerAdapter(jda,
                collectableItemRepository,
                cadernetaRepository
            )

            //val scrapperListener = ScrapperListenerAdapter(jda, collectableRepository)

            return TryCloseable {
                boosterListener.tryClose()
                inventoryListener.tryClose()
                cadernetaListenerAdapter.tryClose()
                //scrapperListener.tryClose()
            }.asResult()

        } catch (e: Exception) {
            log.error("start - failed to initialize CollectorModule", e)
            return Either.Left(ThrowableError(e))
        }
    }
}