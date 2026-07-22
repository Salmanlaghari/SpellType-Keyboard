package com.spelltype.keyboard.domain

import com.spelltype.keyboard.domain.model.FrameStyle

enum class KeyboardMood(val displayName: String, val emoji: String) {
    HAPPY("Happy", "😊"),
    FUNNY("Funny", "😂"),
    EXCITED("Excited", "🎉"),
    LOVE("Love", "❤️"),
    COOL("Cool", "😎"),
    NEUTRAL("Neutral", "✨")
}

data class MoodSuggestion(
    val mood: KeyboardMood,
    val suggestedStyles: List<FrameStyle>
)

object MoodDetector {

    private val happyKeywords = setOf("happy", "smile", "good", "great", "nice", "yes", "joy", "cheerful", "glad", "blessed", "pleasant")
    private val funnyKeywords = setOf("lol", "haha", "lmao", "funny", "joke", "fun", "heha", "xd", "meme", "silly")
    private val excitedKeywords = setOf("excited", "wow", "omg", "awesome", "yay", "hurray", "epic", "superb", "dazzling", "thrilled", "victory")
    private val loveKeywords = setOf("love", "heart", "cute", "sweet", "kiss", "xoxo", "honey", "darling", "babe", "miss", "lovely", "adore")
    private val coolKeywords = setOf("cool", "bro", "bhai", "chill", "style", "obsidian", "dark", "dope", "swagger", "smart", "rad", "slick")

    fun detectMood(text: String): MoodSuggestion {
        if (text.isEmpty()) {
            return MoodSuggestion(KeyboardMood.NEUTRAL, listOf(FrameStyle.STAR, FrameStyle.BOX_ROUNDED))
        }

        val lowercaseText = text.lowercase()
        val words = lowercaseText.split(Regex("[^a-zA-Z]+")).filter { it.isNotEmpty() }

        var happyCount = 0
        var funnyCount = 0
        var excitedCount = 0
        var loveCount = 0
        var coolCount = 0

        for (word in words) {
            if (happyKeywords.contains(word)) happyCount++
            if (funnyKeywords.contains(word)) funnyCount++
            if (excitedKeywords.contains(word)) excitedCount++
            if (loveKeywords.contains(word)) loveCount++
            if (coolKeywords.contains(word)) coolCount++
        }

        val counts = mapOf(
            KeyboardMood.HAPPY to happyCount,
            KeyboardMood.FUNNY to funnyCount,
            KeyboardMood.EXCITED to excitedCount,
            KeyboardMood.LOVE to loveCount,
            KeyboardMood.COOL to coolCount
        )

        val maxEntry = counts.entries.maxByOrNull { it.value }

        if (maxEntry == null || maxEntry.value == 0) {
            return MoodSuggestion(
                KeyboardMood.NEUTRAL,
                listOf(FrameStyle.STAR, FrameStyle.BOX_ROUNDED, FrameStyle.DIAMOND, FrameStyle.DASHED)
            )
        }

        return when (maxEntry.key) {
            KeyboardMood.HAPPY -> MoodSuggestion(
                KeyboardMood.HAPPY,
                listOf(FrameStyle.FLOWERS, FrameStyle.SUN, FrameStyle.CLOVER, FrameStyle.FLORAL)
            )
            KeyboardMood.FUNNY -> MoodSuggestion(
                KeyboardMood.FUNNY,
                listOf(FrameStyle.PARTY, FrameStyle.BALLOON, FrameStyle.GHOST)
            )
            KeyboardMood.EXCITED -> MoodSuggestion(
                KeyboardMood.EXCITED,
                listOf(FrameStyle.SPARKS, FrameStyle.CROWN, FrameStyle.FIRE)
            )
            KeyboardMood.LOVE -> MoodSuggestion(
                KeyboardMood.LOVE,
                listOf(FrameStyle.HEARTS, FrameStyle.FLOWERS)
            )
            KeyboardMood.COOL -> MoodSuggestion(
                KeyboardMood.COOL,
                listOf(FrameStyle.DIAMOND, FrameStyle.GEM, FrameStyle.CROWN, FrameStyle.THICK)
            )
            else -> MoodSuggestion(
                KeyboardMood.NEUTRAL,
                listOf(FrameStyle.STAR, FrameStyle.BOX_ROUNDED)
            )
        }
    }
}
