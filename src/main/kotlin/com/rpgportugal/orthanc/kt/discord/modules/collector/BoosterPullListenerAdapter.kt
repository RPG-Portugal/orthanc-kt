package com.rpgportugal.orthanc.kt.discord.modules.collector

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.rpgportugal.orthanc.kt.discord.listener.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.BoosterType
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.CollectableItem
import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.UserCollectorProfile
import com.rpgportugal.orthanc.kt.error.DomainError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.logging.Loggable
import com.rpgportugal.orthanc.kt.logging.log
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.BoosterTypeRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.CollectableItemRepository
import com.rpgportugal.orthanc.kt.persistence.repository.collectable.UserCollectorProfileRepository
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import java.awt.Color
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class BoosterPullListenerAdapter(
    private val jda: JDA,
    private val boosterTypeRepository: BoosterTypeRepository,
    private val userCollectorProfileRepository: UserCollectorProfileRepository,
    private val collectableItemRepository: CollectableItemRepository,
) : CloseableListenerAdapter(), Loggable {

    init {
        jda.addEventListener(this)
    }

    val cachedBoosterPulls: Cache<Long, StoredBoosterPull> = Caffeine.newBuilder()
        .expireAfterAccess(15.minutes.toJavaDuration())
        .build<Long, StoredBoosterPull>()

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
            message.lowercase().startsWith("\$booster")||
                    message.lowercase().startsWith("\$pull")-> {
                try {
                    doPull(event)
                } catch (e: Exception) {
                    log.error("Error during booster pull", e)
                    event.channel.sendMessage("Erro ao abrir o booster!").queue()
                }
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId == "test_button") {
            event.reply("Button clicked!").queue()
        } else if (event.componentId.startsWith("col_pull_")) {
            val messageId = event.componentId.split("_")[2].toLong()
            val proximaPagina = event.componentId.split("_")[3].toInt()

            val pull = cachedBoosterPulls.getIfPresent(messageId) ?: run {
                event.reply("Este resultado expirou.").setEphemeral(true).queue()
                throw Exception("Expired pull")
            }

            if (pull.userId != (event.member?.id ?: "-1")) {
                event.reply("Estes botões não são para ti!").setEphemeral(true).queue()
                return
            }


            val novoEmbed: MessageEmbed = makeBoosterPullEmbed(messageId, proximaPagina)
            val btnPrevious =
                Button.primary("col_pull_${messageId}_" + (proximaPagina - 1), "⬅️ Anterior").withDisabled(proximaPagina == 0)
            val btnNext = Button.primary("col_pull_${messageId}_" + (proximaPagina + 1), "Seguinte ➡️")
                .withDisabled(proximaPagina == pull.collectables.size - 1)

            event.editMessageEmbeds(novoEmbed)
                .setActionRow(
                    btnPrevious, btnNext
                ).queue()
        }
    }

    private fun doPull(event: MessageReceivedEvent) {
        val discordId = event.author.id

        val userCollectorProfile: UserCollectorProfile =
            userCollectorProfileRepository.findById(discordId).getOrNull() ?: UserCollectorProfile(id = discordId)

        if (userCollectorProfile.lastBoosterPulledAt?.toLocalDate() == LocalDate.now() && event.author.id != "133647011424501761") {
            event.channel.sendMessage("Já abriste um booster hoje! Volta amanhã.").queue()
            return
        }

        val boosters = boosterTypeRepository.findAll().getOrNull() ?: return
        val randomBooster = boosters.random()

        val collectables = randomBooster.slots.flatMap { slot ->
            (1..slot.cardCount).map {
                slot.collectables.random()
            }
        }.sortedBy { it.rarityTier?.displayOrder ?: 0 }

        // Save collected collectables
        collectables.forEach { collectable ->
            CollectableItem(
                discordUserId = discordId,
                collectable = collectable,
                acquiredAt = LocalDateTime.now(),
                variance = "",
            ).let { item ->
                collectableItemRepository.save(item).fold(
                    { error -> log.error("Failed to save collectable item {}", error) },
                    { saved -> log.info("Saved collectable item {}", saved) }
                )
            }
        }

        // Update profile
        userCollectorProfile.lastBoosterPulledAt = LocalDateTime.now()
        userCollectorProfile.totalBoostersPulled++
        userCollectorProfileRepository.save(userCollectorProfile).fold(
            { error -> log.error("Failed to save profile {}", error) },
            { saved -> log.info("Saved profile {}", saved) }
        )

        val messageId = event.message.idLong
        cachedBoosterPulls.put(messageId,
            StoredBoosterPull(
                userId = discordId,
                pullTime = LocalDateTime.now(),
                boosterType = randomBooster,
                collectables = collectables
            )
        )

        event.message.replyEmbeds(makeBoosterPullSummaryEmbed(messageId)).queue()

        val btnPrevious = Button.primary("col_pull_${messageId}_-1", "⬅️ Anterior").withDisabled(true)
        val btnNext = Button.primary("col_pull_${messageId}_1", "Seguinte ➡️").withDisabled(1 == collectables.size)
        val embed = makeBoosterPullEmbed(messageId, 0)
        event.message.replyEmbeds(embed)
            .setActionRow(
                btnPrevious, btnNext
            )
            .queue()

    }

    private fun makeBoosterPullEmbed(id: Long, index: Int) : MessageEmbed {
        val pull = cachedBoosterPulls.getIfPresent(id) ?: throw Exception("Pull not found")
        val collectible = pull.collectables[index]
        return EmbedBuilder()
            .setTitle("(${collectible.rarityTier?.displaySymbol}) ${collectible.name}")
            .setImage(collectible.imageUrl)
            .setColor(Color.decode(collectible.rarityTier?.color ?: "#FFFFFF"))
            .setDescription("""
                Raridade: ${collectible.rarityTier?.name}
                Link: ${collectible.url}
                Booster: ${pull.boosterType.name}
            """.trimIndent())
            .setFooter("Página ${index + 1} de ${pull.collectables.size}")
            .build()
    }
    private fun makeBoosterPullSummaryEmbed(id: Long) : MessageEmbed {
        val pull = cachedBoosterPulls.getIfPresent(id) ?: throw Exception("Pull not found")
        val collectibles = pull.collectables
        return EmbedBuilder()
            .setTitle("Resumo do Booster")
            .setColor(Color.decode(collectibles.maxBy { it.rarityTier?.displayOrder ?: 0 }.rarityTier?.color ?: "#FFFFFF"))
            .also {
                collectibles.forEach { collectable ->
                    it.addField("**${collectable.id} - (${collectable.rarityTier?.name})**", "${collectable.name}", false)
                }
            }
            .setFooter("Booster: ${pull.boosterType.name} // Aberto a: ${pull.pullTime}")
            .build()
    }

    //Helper subclass
    class StoredBoosterPull(
        val userId: String,
        val pullTime: LocalDateTime,
        val boosterType: BoosterType,
        val collectables: List<Collectable>
    )
}