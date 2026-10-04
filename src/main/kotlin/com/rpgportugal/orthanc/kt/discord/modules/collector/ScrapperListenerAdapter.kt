package com.rpgportugal.orthanc.kt.discord.modules.collector

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.CollectableItem
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.RarityTier
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.UserCollectorProfile
import com.rpgportugal.orthanc.kt.discord.modules.collector.scripts.GamesETL
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.BoosterTypeRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.UserCollectorProfileRepository
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.collections.set

class ScrapperListenerAdapter(
    private val jda: JDA,
    private val collectableRepository: CollectableRepository,
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    override fun tryClose(): DomainError? {
        try {
            jda.removeEventListener(this)
            return null
        } catch (exception: Exception) {
            log.error("tryClose - failed to close", exception)
            return ThrowableError(exception)
        }
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {

        val message = event.message.contentStripped

        when{
            message.lowercase().startsWith("\$scrape") -> {
                runBlocking {
                    println("Starting")
                    val games = GamesETL.scrapeGamesFromAllCategories()

                    var id = 1000L
                    val sb = StringBuilder()
                    sb.append("insert into col_collectable (id, name, description, image_url, created_at, system_type, rarity_tier_id, url)\n")
                    for (game in games) {
                        println(game.name)
                        game.id = id++
                        game.name = game.name.replace("'", "''")
                        game.description = game.description.replace("'", "''")
                        game.rarityTier = RarityTier(id = 1)

                        sb.append("(${game.id}, '${game.name}', '${game.description}', '${game.imageUrl}', '${game.createdAt}', '${game.systemType}', ${game.rarityTier?.id}, '${game.url}'),\n")
                    }
                    println(sb.toString())
                    println("Finished.")
                }
            }
        }
    }
}