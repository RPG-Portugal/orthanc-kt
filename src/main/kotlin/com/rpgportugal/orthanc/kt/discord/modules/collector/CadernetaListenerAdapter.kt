package com.rpgportugal.orthanc.kt.discord.modules.collector

import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CadernetaRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class CadernetaListenerAdapter(
    private val jda: JDA,
    private val collectableItemRepository: CollectableItemRepository,
    private val cadernetaRepository: CadernetaRepository
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
            message.lowercase().startsWith("\$caderneta") -> {
                try {
                    doCaderneta(event)
                } catch (e: Exception) {
                    log.error("Error during inventory display", e)
                    event.channel.sendMessage("Erro ao abrir o inventário!").queue()
                }
            }
        }
    }

    private fun doCaderneta(event: MessageReceivedEvent) {
        val userId = event.author.id

        val caderneta = cadernetaRepository.findAll().getOrNull()?.filter { it.enabled }?.first() ?: run {
            event.message.reply("Não há cadernetas ativas.").queue()
            return@doCaderneta
        }

        val collectableItems = collectableItemRepository.findByDiscordUserId(userId).getOrNull() ?: listOf()

        val nrCollected = caderneta.collectables.count { collectable -> collectableItems.any { it.collectable?.id == collectable.id } }

        val embed = EmbedBuilder()
            .setTitle("Caderneta")
            .setDescription("""
                ## **Nome: ${caderneta.name}**
                ## Descrição: ${caderneta.description}
                
                ## **Lista:**
            """.trimIndent())
            .also {
                caderneta.collectables.forEach { collectable ->
                    val collectedAt = collectableItems.find { userColItem -> userColItem.collectable?.id == collectable.id }?.acquiredAt
                    val stroke = if(collectedAt != null) {"~~"} else {""}
                    val desc = if(collectedAt != null) {"\n\n**Obtido em:** ${collectedAt}"} else {"Não Obtido"}
                    val emoji = if(collectedAt != null) {":white_check_mark:"} else {"❌"}
                    it.addField("$emoji ${stroke}**${collectable.id} - (${collectable.rarityTier?.displaySymbol}) ${collectable.name}**${stroke}",
                        desc, false)
                }
            }
            .setFooter("Total de TTRPGs na Caderneta obtidos: ${nrCollected}/${caderneta.collectables.size}")
            .build()
            event.message.replyEmbeds(embed).queue()
    }

}