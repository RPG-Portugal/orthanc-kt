package com.rpgportugal.orthanc.kt.discord.modules.collector.scripts

import com.rpgportugal.orthanc.kt.discord.modules.collector.dto.Collectable
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class GamesETL {
    companion object {
        private const val TTRPG_GAMES_URL = "https://www.ttrpg-games.com"
        private const val CATEGORIES_URL = "$TTRPG_GAMES_URL/categories"
        private const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
        private const val REQUEST_DELAY_MS = 500L

        suspend fun scrapeGamesFromAllCategories(): List<Collectable> {
            val allGames = mutableListOf<Collectable>()
            val seenNames = mutableSetOf<String>()

            println("Fetching categories from $CATEGORIES_URL")
            val categoriesDoc = fetchPageWithRetry(CATEGORIES_URL)
            val categoryUrls = extractCategoryUrls(categoriesDoc)

            println("Found ${categoryUrls.size} categories")

            categoryUrls.forEach { categoryUrl ->
                try {
                    println("Scraping category: $categoryUrl")
                    val categoryDoc = fetchPageWithRetry(categoryUrl)
                    val games = parseGameCards(categoryDoc)

                    games.forEach { game ->
                        if (!seenNames.contains(game.name)) {
                            allGames.add(game)
                            seenNames.add(game.name)
                        }
                    }
                    println("  -> Found ${games.size} unique games from this category")
                } catch (e: Exception) {
                    println("Failed to scrape category $categoryUrl: ${e.message}")
                }
            }

            println("Total games collected: ${allGames.size}")
            return allGames
        }

        suspend fun scrapeGamesFromTTRPGGames(): List<Collectable> {
            val document = fetchPageWithRetry(TTRPG_GAMES_URL)
            return parseGameCards(document)
        }

        private suspend fun fetchPageWithRetry(url: String, retries: Int = 3): Document {
            var lastException: Exception? = null
            repeat(retries) { attempt ->
                try {
                    delay(REQUEST_DELAY_MS)
                    return Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(10000)
                        .get()
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < retries - 1) {
                        delay((REQUEST_DELAY_MS * (attempt + 2)).milliseconds)
                    }
                }
            }
            throw lastException ?: Exception("Failed to fetch page after $retries retries")
        }

        private fun extractCategoryUrls(document: Document): List<String> {
            val categoryUrls = mutableListOf<String>()
            val seenUrls = mutableSetOf<String>()

            // All links that contain /category/ in the href
            val allLinks = document.select("a[href*='/category']")

            println("Found ${allLinks.size} potential category links")

            allLinks.forEach { link ->
                val href = link.attr("href")
                if (href.isNotBlank()) {
                    // Filter out links that are not category pages
                    val isCategoryLink = href.matches(Regex(".*?/category/[a-z0-9\\-]+/?$")) ||
                                         href.matches(Regex(".*?/category/[a-z0-9\\-]+\\?.*"))

                    if (isCategoryLink) {
                        // Normalize URL and extract just the path
                        val normalized = when {
                            href.startsWith("http") -> href
                            href.startsWith("/") -> TTRPG_GAMES_URL + href
                            else -> TTRPG_GAMES_URL + "/" + href
                        }

                        // Remove query parameters for deduplication
                        val cleanUrl = normalized.split("?")[0].removeSuffix("/")

                        if (!seenUrls.contains(cleanUrl) && cleanUrl != CATEGORIES_URL) {
                            categoryUrls.add(cleanUrl)
                            seenUrls.add(cleanUrl)
                            println("  -> Added category: $cleanUrl")
                        }
                    }
                }
            }

            return categoryUrls
        }

        private fun parseGameCards(document: Document): List<Collectable> {
            val games = mutableListOf<Collectable>()
            val seenNames = mutableSetOf<String>()

            // Games are linked with /item/ URLs on category pages
            val gameLinks = document.select("a[href*='/item/']")

            println("Found ${gameLinks.size} game links on this page")

            val elementsToProcess = gameLinks.map { link ->
                // Wrap the link in a container that has both the link and any following description
                val parent = link.parent()
                if (parent != null) parent else link
            }

            elementsToProcess.forEach { element ->
                try {
                    val collectable = parseGameElement(element)
                    if (collectable != null && !seenNames.contains(collectable.name)) {
                        games.add(collectable)
                        seenNames.add(collectable.name)
                    }
                } catch (e: Exception) {
                    // Log and continue on individual parse failures
                    println("Failed to parse game element: ${e.message}")
                }
            }

            return games
        }

        private fun parseGameElement(element: Element): Collectable? {
            // Extract name
            val name = extractName(element) ?: return null

            // Extract description
            val description = extractDescription(element) ?: ""

            // Extract image URL
            val imageUrl = extractImageUrl(element)

            // Extract game URL (relative paths converted to absolute)
            val gameUrl = extractGameUrl(element)

            // Detect system type from content
            val systemType = detectSystemType(element, name, description)

            return Collectable(
                name = name,
                description = description,
                systemType = systemType,
                imageUrl = imageUrl,
                url = gameUrl,
                createdAt = LocalDateTime.now()
            )
        }

        private fun extractName(element: Element): String? {
            // Get the game name from the /item/ link - this is the only reliable source
            return element.selectFirst("a[href*='/item/']")?.text()?.takeIf { it.isNotBlank() }?.trim()
        }

        private fun extractDescription(element: Element): String? {
            // Try to find description in various elements
            var description = element.selectFirst(
                "[class*='description'], [class*='summary'], [class*='synopsis']"
            )?.text()?.takeIf { it.isNotBlank() }?.trim()

            if (!description.isNullOrBlank()) {
                return description
            }

            // Look for any paragraph after the title/link
            description = element.selectFirst("p")?.text()?.takeIf { it.isNotBlank() }?.trim()
            if (!description.isNullOrBlank()) {
                return description
            }

            // Get all text content and extract description heuristically
            val fullText = element.text()
            val parts = fullText.split(" - ", limit = 2)
            if (parts.size == 2) {
                return parts[1].take(200).trim()
            }

            return if (fullText.length > 50) fullText.take(200).trim() else null
        }

        private fun extractImageUrl(element: Element): String? {
            // Try to find img tag with src
            var imgElement = element.selectFirst("img[src]")
            if (imgElement != null) {
                val url = imgElement.attr("src")
                if (url.isNotBlank()) {
                    return normalizeUrl(url)
                }
            }

            // Try img with data-src (lazy loading)
            imgElement = element.selectFirst("img[data-src]")
            if (imgElement != null) {
                val url = imgElement.attr("data-src")
                if (url.isNotBlank()) {
                    return normalizeUrl(url)
                }
            }

            // Try to extract from background-image style
            imgElement = element.selectFirst("[style*='background-image']")
            if (imgElement != null) {
                val style = imgElement.attr("style")
                val urlMatch = Regex("url\\(['\"]?([^'\"\\)]+)['\"]?\\)").find(style)
                if (urlMatch != null) {
                    val url = urlMatch.groupValues[1]
                    if (url.isNotBlank()) {
                        return normalizeUrl(url)
                    }
                }
            }

            // Try to construct from the item slug if we can get it
            val itemLink = element.selectFirst("a[href*='/item/']")
            if (itemLink != null) {
                val href = itemLink.attr("href")
                val slug = href.replace(Regex(".*?/item/([^/?]+).*"), "$1")
                if (slug != href && slug.isNotBlank()) {
                    // Try to construct API URL
                    return "$TTRPG_GAMES_URL/api/hero-image/$slug"
                }
            }

            return null
        }

        private fun extractGameUrl(element: Element): String? {
            // If the element itself is a link, use it
            if (element.tagName() == "a") {
                val href = element.attr("href")
                if (href.isNotBlank()) {
                    return normalizeUrl(href)
                }
            }

            // Look for /item/ links first (games), then any other links
            var linkElement = element.selectFirst("a[href*='/item/']")
            if (linkElement == null) {
                linkElement = element.selectFirst("a[href]")
            }

            return linkElement?.attr("href")?.let { href ->
                normalizeUrl(href)
            }
        }

        private fun normalizeUrl(url: String): String {
            return when {
                url.startsWith("http://") || url.startsWith("https://") -> url
                url.startsWith("/") -> TTRPG_GAMES_URL + url
                else -> TTRPG_GAMES_URL + "/" + url
            }
        }

        private fun detectSystemType(element: Element, name: String, description: String): String {
            val text = (name + " " + description).lowercase()

            // Map common system keywords
            return when {
                text.contains("d&d") || text.contains("dungeons and dragons") -> "D&D"
                text.contains("pathfinder") -> "PATHFINDER"
                text.contains("call of cthulhu") -> "COC"
                text.contains("world of darkness") || text.contains("vampire") -> "WOD"
                text.contains("shadowrun") -> "SHADOWRUN"
                text.contains("warhammer") -> "WARHAMMER"
                text.contains("starfinder") -> "STARFINDER"
                text.contains("cyberpunk") -> "CYBERPUNK"
                text.contains("blades in the dark") -> "BITD"
                text.contains("fate") -> "FATE"
                text.contains("powered by the apocalypse") || text.contains("pbta") -> "PBTA"
                text.contains("d20") -> "D20"
                text.contains("savage worlds") -> "SAVAGE_WORLDS"
                text.contains("gurps") -> "GURPS"
                else -> "OTHER"
            }
        }
    }
}
