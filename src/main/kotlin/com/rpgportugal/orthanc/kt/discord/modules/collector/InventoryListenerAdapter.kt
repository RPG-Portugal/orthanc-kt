package com.rpgportugal.orthanc.kt.discord.modules.collector

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.CollectableItem
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import kotlin.math.ceil
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class InventoryListenerAdapter(
    private val jda: JDA,
    private val collectableItemRepository: CollectableItemRepository,
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    val pageSize = 20

    val cachedInventory: Cache<Long, InventoryData> = Caffeine.newBuilder()
        .expireAfterAccess(15.minutes.toJavaDuration())
        .build<Long, InventoryData>()

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
            message.lowercase().startsWith("\$inventario") -> {
                try {
                    doInv(event)
                } catch (e: Exception) {
                    log.error("Error during inventory display", e)
                    event.channel.sendMessage("Erro ao abrir o inventário!").queue()
                }
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId.startsWith("col_inv_")) {
            val userId = event.componentId.split("_")[2].toLong()
            val nextPage = event.componentId.split("_")[3].toInt()

            val inventoryData = cachedInventory.getIfPresent(userId) ?: run {
                event.reply("Este resultado expirou.").setEphemeral(true).queue()
                throw Exception("Expired inventory")
            }

            if (userId.toString() != (event.member?.id ?: "-1")) {
                event.reply("Estes botões não são para ti!").setEphemeral(true).queue()
                return
            }

            val novoEmbed: MessageEmbed = makeInventoryEmbed(userId, nextPage)
            val btnPrevious =
                Button.primary("col_inv_${userId}_" + (nextPage - 1), "⬅️ Anterior").withDisabled(nextPage == 0)
            val btnNext = Button.primary("col_inv_${userId}_" + (nextPage + 1), "Seguinte ➡️")
                .withDisabled(nextPage == ceil(inventoryData.collectables.size.toDouble()/pageSize).toInt() -1)

            event.editMessageEmbeds(novoEmbed)
                .setActionRow(
                    btnPrevious, btnNext
                ).queue()
        }
    }

    private fun doInv(event: MessageReceivedEvent) {
        val discordId = event.author.id

        collectableItemRepository.findByDiscordUserId(discordId).fold(
            { error ->
                log.error("Error fetching collectables for user $discordId", error)
                event.channel.sendMessage("Erro ao abrir o inventário!").queue()
            },
            { collectables ->
                if (collectables.isEmpty()) {
                    event.channel.sendMessage("Você não possui nenhum item colecionável!").queue()
                } else {

                    val userId = event.author.idLong
                    cachedInventory.put(userId, InventoryData(userId, collectables))

                    val embed = makeInventoryEmbed(userId)

                    val btnPrevious =
                        Button.primary("col_inv_${userId}_" + (0 - 1), "⬅️ Anterior").withDisabled(true)
                    val btnNext = Button.primary("col_inv_${userId}_" + (0 + 1), "Seguinte ➡️")
                        .withDisabled(0 == ceil(collectables.size.toDouble()/pageSize).toInt() -1)

                    event.channel.sendMessageEmbeds(embed)
                        .setActionRow(btnPrevious, btnNext)
                        .queue()

                }
            }
        )

    }

    private fun makeInventoryEmbed(userId:Long, index:Int = 0) : MessageEmbed {

        val inventoryData = cachedInventory.getIfPresent(userId) ?: throw Exception("Inventory not found")
        val collectables = inventoryData.collectables
        val startIndex = index * pageSize
        val endIndex = minOf(startIndex + pageSize, collectables.size)
        val pageCollectables = collectables.subList(startIndex, endIndex)

        return EmbedBuilder()
            .setTitle("Inventário")
            .also {
                pageCollectables.forEach { collectable ->
                    it.addField("**${collectable.id} - (${collectable.collectable?.rarityTier?.displaySymbol}) ${collectable.collectable?.name}**", "${collectable.acquiredAt}", false)
                }
            }
            .setFooter("Total: ${collectables.size}")
            .build()
    }

    class InventoryData(
        val userId: Long,
        val collectables: List<CollectableItem>
    )
}