package com.spelltype.keyboard.domain.ai

/**
 * AI Suggestions Engine — Gboard-style word prediction + smart completions
 * Uses n-gram frequency analysis and contextual prediction
 */
object AISuggestionsEngine {

    // Common word pairs for contextual prediction (bigram model)
    private val bigrams = mapOf(
        "i" to listOf("am", "want", "need", "love", "have", "think", "know", "can", "will", "would", "should"),
        "the" to listOf("best", "most", "first", "last", "next", "new", "old", "big", "small", "great"),
        "how" to listOf("are", "is", "do", "did", "can", "much", "many", "long", "old", "far"),
        "what" to listOf("is", "are", "do", "did", "time", "happened", "going", "about"),
        "let" to listOf("me", "us", "it", "them", "you", "go", "see"),
        "good" to listOf("morning", "night", "day", "job", "idea", "luck", "work"),
        "thank" to listOf("you", "god", "goodness", "u"),
        "please" to listOf("help", "send", "check", "let", "call"),
        "can" to listOf("you", "i", "we", "they", "it", "help", "see"),
        "i'm" to listOf("going", "coming", "looking", "trying", "working", "thinking"),
        "don't" to listOf("know", "think", "want", "have", "worry", "care"),
        "it's" to listOf("a", "the", "not", "been", "all", "ok"),
        "that's" to listOf("great", "awesome", "good", "nice", "cool", "fine"),
        "looking" to listOf("for", "forward", "at", "good", "great"),
        "happy" to listOf("birthday", "new", "to", "day"),
        "love" to listOf("you", "this", "it", "the", "my"),
        "miss" to listOf("you", "the", "my", "those"),
        "want" to listOf("to", "a", "the", "you"),
        "need" to listOf("to", "a", "the", "help", "you"),
        "going" to listOf("to", "home", "out", "back"),
        "come" to listOf("to", "on", "here", "back", "over"),
        "get" to listOf("the", "a", "ready", "well", "better"),
        "make" to listOf("it", "sure", "a", "me"),
        "take" to listOf("a", "the", "care", "it"),
        "give" to listOf("me", "us", "it", "a"),
        "tell" to listOf("me", "us", "them", "you"),
        "see" to listOf("you", "the", "it", "me"),
        "go" to listOf("to", "home", "out", "back", "ahead"),
        "come" to listOf("on", "here", "back", "over", "to")
    )

    // Common word completions by prefix
    private val completions = mapOf(
        "hel" to listOf("hello", "help", "helpful"),
        "goo" to listOf("good", "google", "goodbye"),
        "hap" to listOf("happy", "happen", "happiness"),
        "tha" to listOf("thank", "that", "thanks"),
        "ple" to listOf("please", "pleasant"),
        "mis" to listOf("miss", "mission", "missing"),
        "bea" to listOf("beautiful", "beast", "beach"),
        "lov" to listOf("love", "lovely", "loving"),
        "wri" to listOf("write", "writing", "writer"),
        "cal" to listOf("call", "calling", "calendar"),
        "wor" to listOf("work", "world", "worry", "working"),
        "liv" to listOf("live", "living", "liver"),
        "fee" to listOf("feel", "feeling", "feed"),
        "loo" to listOf("look", "looking", "loop"),
        "kno" to listOf("know", "knowledge", "known"),
        "thi" to listOf("think", "this", "things"),
        "wan" to listOf("want", "wanna", "wanted"),
        "nee" to listOf("need", "needed"),
        "wou" to listOf("would", "wouldn't"),
        "cou" to listOf("could", "couldn't", "count"),
        "sho" to listOf("should", "show", "short"),
        "som" to listOf("some", "something", "someone"),
        "eve" to listOf("everything", "everyone", "evening"),
        "any" to listOf("anything", "anyone", "anyway"),
        "mee" to listOf("meeting", "meet"),
        "tom" to listOf("tomorrow"),
        "ton" to listOf("tonight"),
        "int" to listOf("interested", "into", "internet"),
        "imp" to listOf("important", "impossible", "improve"),
        "pic" to listOf("picture", "pick"),
        "mes" to listOf("message", "mess"),
        "con" to listOf("contact", "continue", "consider"),
        "pro" to listOf("probably", "problem", "project"),
        "app" to listOf("appreciate", "app", "apply"),
        "fin" to listOf("find", "finish", "fine"),
        "tes" to listOf("test", "testing"),
        "bui" to listOf("build", "building", "built"),
        "dev" to listOf("developer", "development", "device"),
        "cod" to listOf("code", "coding"),
        "des" to listOf("design", "developer"),
        "upd" to listOf("update", "updated"),
        "com" to listOf("come", "complete", "community"),
        "bat" to listOf("battery", "battle"),
        "net" to listOf("network", "net"),
        "sig" to listOf("sign", "signal", "signature"),
        "con" to listOf("connect", "contact", "continue"),
        "mus" to listOf("music", "must"),
        "gam" to listOf("game", "gaming"),
        "vid" to listOf("video", "videos"),
        "pha" to listOf("photo", "photos", "phone"),
        "sto" to listOf("story", "store", "stop"),
        "tra" to listOf("translate", "travel", "track"),
        "wea" to listOf("weather", "wear"),
        "mar" to listOf("market", "marketing")
    )

    // Emoji suggestions by keyword
    private val emojiMap = mapOf(
        "happy" to "😊", "sad" to "😢", "love" to "❤️", "fire" to "🔥",
        "cool" to "😎", "laugh" to "😂", "cry" to "😭", "angry" to "😡",
        "think" to "🤔", "surprise" to "😮", "party" to "🎉", "star" to "⭐",
        "heart" to "❤️", "broken" to "💔", "sun" to "☀️", "moon" to "🌙",
        "rain" to "🌧️", "snow" to "❄️", "fire" to "🔥", "water" to "💧",
        "food" to "🍕", "pizza" to "🍕", "coffee" to "☕", "tea" to "🍵",
        "music" to "🎵", "game" to "🎮", "phone" to "📱", "computer" to "💻",
        "car" to "🚗", "plane" to "✈️", "home" to "🏠", "work" to "💼",
        "school" to "📚", "book" to "📖", "money" to "💰", "gift" to "🎁",
        "birthday" to "🎂", "christmas" to "🎄", "halloween" to "🎃",
        "cat" to "🐱", "dog" to "🐶", "fish" to "🐟", "bird" to "🐦",
        "tree" to "🌳", "flower" to "🌸", "mountain" to "⛰️", "ocean" to "🌊",
        "yes" to "✅", "no" to "❌", "ok" to "👍", "thanks" to "🙏",
        "sorry" to "😔", "please" to "🥺", "wow" to "🤩", "omg" to "😱",
        "bye" to "👋", "hi" to "👋", "hello" to "👋"
    )

    /**
     * Get word suggestions based on current input
     * Returns up to 3 suggestions (left, center, right)
     */
    fun getSuggestions(currentInput: String, previousWord: String = ""): List<String> {
        if (currentInput.isEmpty()) return emptyList()

        val lower = currentInput.lowercase().trim()
        val results = mutableListOf<String>()

        // 1. Try prefix completion
        for ((prefix, suggestions) in completions) {
            if (lower.startsWith(prefix) || prefix.startsWith(lower)) {
                results.addAll(suggestions.filter { it.startsWith(lower) })
            }
        }

        // 2. Try bigram prediction from previous word
        if (previousWord.isNotEmpty()) {
            val prevLower = previousWord.lowercase()
            bigrams[prevLower]?.let { predicted ->
                results.addAll(predicted.filter { it.startsWith(lower) })
            }
        }

        // 3. Try direct prefix match from all bigrams
        for ((_, suggestions) in bigrams) {
            results.addAll(suggestions.filter { it.startsWith(lower) && !results.contains(it) })
        }

        return results.distinct().take(3)
    }

    /**
     * Get emoji suggestions for the current word
     */
    fun getEmojiSuggestions(word: String): List<String> {
        val lower = word.lowercase().trim()
        if (lower.isEmpty()) return emptyList()

        val suggestions = mutableListOf<String>()

        // Direct match
        emojiMap[lower]?.let { suggestions.add(it) }

        // Partial match
        for ((key, emoji) in emojiMap) {
            if (key.contains(lower) || lower.contains(key)) {
                suggestions.add(emoji)
            }
        }

        return suggestions.distinct().take(5)
    }

    /**
     * Smart autocorrect for common typos
     */
    fun autocorrect(word: String): String? {
        val lower = word.lowercase()
        val corrections = mapOf(
            "teh" to "the", "adn" to "and", "taht" to "that",
            "hte" to "the", "nto" to "not", "fo" to "of",
            "ot" to "to", "si" to "is", "ti" to "it",
            "waht" to "what", "jsut" to "just", "liek" to "like",
            "dont" to "don't", "cant" to "can't", "wont" to "won't",
            "doesnt" to "doesn't", "isnt" to "isn't", "arent" to "aren't",
            "wasnt" to "wasn't", "werent" to "weren't", "hasnt" to "hasn't",
            "havent" to "haven't", "hadnt" to "hadn't", "didnt" to "didn't",
            "wouldnt" to "wouldn't", "shouldnt" to "shouldn't", "couldnt" to "couldn't",
            "im" to "I'm", "ive" to "I've", "id" to "I'd", "ill" to "I'll",
            "your" to "you're", "its" to "it's", "thats" to "that's",
            "whats" to "what's", "lets" to "let's", "thier" to "their",
            "recieve" to "receive", "achive" to "achieve", "beleive" to "believe",
            "occured" to "occurred", "refered" to "referred", "begining" to "beginning",
            "definately" to "definitely", "seperate" to "separate",
            "accomodate" to "accommodate", "occurence" to "occurrence",
            "untill" to "until", "wich" to "which", "becuase" to "because",
            "probaly" to "probably", "neccessary" to "necessary"
        )
        return corrections[lower]
    }

    /**
     * Get next-word prediction based on context
     */
    fun predictNextWord(context: String): List<String> {
        val words = context.trim().split("\\s+".toRegex())
        if (words.isEmpty()) return emptyList()

        val lastWord = words.last().lowercase()
        return bigrams[lastWord] ?: emptyList()
    }
}
