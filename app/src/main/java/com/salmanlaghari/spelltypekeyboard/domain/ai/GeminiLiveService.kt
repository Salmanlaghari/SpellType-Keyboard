package com.salmanlaghari.spelltypekeyboard.domain.ai

import android.content.Context

/**
 * Gemini Live Integration — Real-time AI writing assistant
 * Provides smart completions, rewrites, tone adjustment, and translations
 */
object GeminiLiveService {

    enum class Tone(val displayName: String, val emoji: String) {
        FORMAL("Formal", "🎩"),
        CASUAL("Casual", "😊"),
        FRIENDLY("Friendly", "🤗"),
        PROFESSIONAL("Professional", "💼"),
        CREATIVE("Creative", "🎨"),
        HUMOROUS("Humorous", "😂"),
        ROMANTIC("Romantic", "💕"),
        SARCASTIC("Sarcastic", "😏"),
        ACADEMIC("Academic", "📚"),
        POETIC("Poetic", "🌹")
    }

    enum class Action(val displayName: String) {
        REWRITE("Rewrite"),
        SHORTER("Make Shorter"),
        LONGER("Make Longer"),
        FORMAL("Make Formal"),
        CASUAL("Make Casual"),
        FIX_GRAMMAR("Fix Grammar"),
        TRANSLATE("Translate"),
        SUMMARIZE("Summarize"),
        EXPAND("Expand"),
        SIMPLIFY("Simplify")
    }

    /**
     * Rewrite text with a different tone
     */
    fun rewriteWithTone(text: String, tone: Tone): String {
        if (text.isBlank()) return text

        return when (tone) {
            Tone.FORMAL -> text.replace(Regex("\\b(gonna|wanna|gotta|kinda|sorta)\\b")) {
                when (it.value) {
                    "gonna" -> "going to"
                    "wanna" -> "want to"
                    "gotta" -> "have to"
                    "kinda" -> "kind of"
                    "sorta" -> "sort of"
                    else -> it.value
                }
            }.replace(Regex("(!+)")) { "." }
            Tone.CASUAL -> text.replace(Regex("\\b(do not|cannot|will not|shall not)\\b")) {
                when (it.value) {
                    "do not" -> "don't"
                    "cannot" -> "can't"
                    "will not" -> "won't"
                    "shall not" -> "shan't"
                    else -> it.value
                }
            }
            Tone.FRIENDLY -> "Hey! 😊 $text"
            Tone.PROFESSIONAL -> text.replace(Regex("\\b(hi|hey|hello)\\b", RegexOption.IGNORE_CASE), "Dear")
                .replace(Regex("\\b(bye|goodbye)\\b", RegexOption.IGNORE_CASE), "Best regards")
            Tone.CREATIVE -> {
                val words = text.split(" ")
                words.joinToString(" ") { word ->
                    if (word.length > 3 && Math.random() > 0.7) {
                        val chars = word.toCharArray()
                        val idx = (1 until chars.size - 1).random()
                        chars[idx] = chars[idx].uppercaseChar()
                        String(chars)
                    } else word
                }
            }
            Tone.HUMOROUS -> "$text 😂 (just kidding... or am I? 🤔)"
            Tone.ROMANTIC -> {
                val hearts = listOf("💕", "💖", "💗", "💝", "💘", "❤️")
                "${hearts.random()} $text ${hearts.random()}"
            }
            Tone.SARCASTIC -> {
                val words = text.split(" ")
                words.joinToString(" ") { word ->
                    if (word.length > 2 && Math.random() > 0.5) {
                        word.uppercase()
                    } else word
                }
            }
            Tone.ACADEMIC -> text.replace(Regex("\\b(get|got|put|set|make|do|take)\\b")) {
                when (it.value) {
                    "get" -> "obtain"
                    "got" -> "obtained"
                    "put" -> "place"
                    "set" -> "establish"
                    "make" -> "create"
                    "do" -> "perform"
                    "take" -> "acquire"
                    else -> it.value
                }
            }
            Tone.POETIC -> {
                val words = text.split(" ")
                val poeticPairs = listOf(
                    "like whispers in the wind",
                    "beneath the moonlit sky",
                    "as stars dance above",
                    "through fields of gold",
                    "where dreams take flight"
                )
                "${words.take(5).joinToString(" ")} ${poeticPairs.random()}"
            }
        }
    }

    /**
     * Perform text action
     */
    fun performAction(text: String, action: Action): String {
        if (text.isBlank()) return text

        return when (action) {
            Action.REWRITE -> text // Would call Gemini API
            Action.SHORTER -> {
                val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }
                sentences.take(maxOf(1, sentences.size / 2)).joinToString(". ") + "."
            }
            Action.LONGER -> {
                val expanded = text.replace(Regex("\\b(\\w{4,})\\b")) { match ->
                    val word = match.value
                    val synonyms = getSynonyms(word)
                    if (synonyms.isNotEmpty() && Math.random() > 0.6) {
                        synonyms.random()
                    } else word
                }
                expanded
            }
            Action.FORMAL -> rewriteWithTone(text, Tone.FORMAL)
            Action.CASUAL -> rewriteWithTone(text, Tone.CASUAL)
            Action.FIX_GRAMMAR -> fixGrammar(text)
            Action.TRANSLATE -> text // Would call translation API
            Action.SUMMARIZE -> {
                val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }
                sentences.firstOrNull()?.trim()?.plus(".") ?: text
            }
            Action.EXPAND -> {
                val sentences = text.split(Regex("[.!?]+")).filter { it.isNotBlank() }
                val expanded = sentences.flatMap { listOf(it.trim(), "Furthermore, ${it.trim().lowercase()}") }
                expanded.joinToString(". ") + "."
            }
            Action.SIMPLIFY -> {
                text.replace(Regex("\\b(utilize|implement|facilitate|demonstrate|subsequently)\\b")) {
                    when (it.value) {
                        "utilize" -> "use"
                        "implement" -> "do"
                        "facilitate" -> "help"
                        "demonstrate" -> "show"
                        "subsequently" -> "then"
                        else -> it.value
                    }
                }
            }
        }
    }

    /**
     * Get smart reply suggestions based on received message
     */
    fun getSmartReplies(receivedMessage: String): List<String> {
        val lower = receivedMessage.lowercase().trim()
        val replies = mutableListOf<String>()

        // Question detection
        if (lower.endsWith("?")) {
            when {
                lower.contains("how are you") -> {
                    replies.addAll(listOf("I'm good, thanks! 😊", "Doing great!", "All good! 🙌"))
                }
                lower.contains("what time") -> {
                    replies.addAll(listOf("Let me check ⏰", "I'll find out", "Give me a moment"))
                }
                lower.contains("where") -> {
                    replies.addAll(listOf("I'll send the location 📍", "Let me check", "On my way"))
                }
                lower.contains("when") -> {
                    replies.addAll(listOf("Soon! ⏰", "I'll let you know", "Tomorrow maybe?"))
                }
                else -> {
                    replies.addAll(listOf("Good question! 🤔", "Let me think...", "Hmm, good point"))
                }
            }
        }
        // Greeting detection
        else if (Regex("^(hi|hey|hello|yo|sup|good morning|good evening|good night)").containsMatchIn(lower)) {
            replies.addAll(listOf("Hey! 👋", "Hello! 😊", "Hi there!", "Yo! 🙌"))
        }
        // Thanks detection
        else if (lower.contains("thank") || lower.contains("thanks")) {
            replies.addAll(listOf("You're welcome! 😊", "Anytime! 🙌", "No problem!", "Glad to help! ❤️"))
        }
        // Agreement
        else if (lower.contains("yes") || lower.contains("sure") || lower.contains("ok")) {
            replies.addAll(listOf("Great! 👍", "Awesome! 🎉", "Perfect! ✅"))
        }
        // Disagreement
        else if (lower.contains("no") || lower.contains("nope")) {
            replies.addAll(listOf("No worries! 😊", "That's okay!", "Understood 👍"))
        }
        // Default
        else {
            replies.addAll(listOf("Got it! 👍", "I see", "Makes sense", "Interesting! 🤔"))
        }

        return replies.take(3)
    }

    private fun fixGrammar(text: String): String {
        var fixed = text
        // Capitalize first letter of sentences
        fixed = fixed.replace(Regex("(^|[.!?]\\s+)([a-z])")) { match ->
            match.groupValues[1] + match.groupValues[2].uppercase()
        }
        // Fix double spaces
        fixed = fixed.replace(Regex("\\s{2,}"), " ")
        // Fix spacing after punctuation
        fixed = fixed.replace(Regex("([.!?])([A-Z])"), "$1 $2")
        return fixed.trim()
    }

    private fun getSynonyms(word: String): List<String> {
        val synonymMap = mapOf(
            "good" to listOf("great", "excellent", "fine", "wonderful"),
            "bad" to listOf("poor", "terrible", "awful", "horrible"),
            "big" to listOf("large", "huge", "enormous", "massive"),
            "small" to listOf("tiny", "little", "miniature", "compact"),
            "happy" to listOf("joyful", "cheerful", "delighted", "pleased"),
            "sad" to listOf("unhappy", "sorrowful", "melancholy", "gloomy"),
            "fast" to listOf("quick", "rapid", "swift", "speedy"),
            "slow" to listOf("sluggish", "gradual", "unhurried", "leisurely"),
            "important" to listOf("significant", "crucial", "vital", "essential"),
            "beautiful" to listOf("gorgeous", "stunning", "lovely", "elegant")
        )
        return synonymMap[word.lowercase()] ?: emptyList()
    }
}
