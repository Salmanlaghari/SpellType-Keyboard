package com.spelltype.keyboard

import com.spelltype.keyboard.domain.KeyboardMood
import com.spelltype.keyboard.domain.MoodDetector
import com.spelltype.keyboard.domain.model.FrameStyle
import org.junit.Assert.*
import org.junit.Test

class MoodDetectorTest {

    @Test
    fun testNeutralEmptyInput() {
        val suggestion = MoodDetector.detectMood("")
        assertEquals(KeyboardMood.NEUTRAL, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.isNotEmpty())
    }

    @Test
    fun testHappyMood() {
        val text = "I am so happy and joyful today"
        val suggestion = MoodDetector.detectMood(text)
        assertEquals(KeyboardMood.HAPPY, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.contains(FrameStyle.FLOWERS) || suggestion.suggestedStyles.contains(FrameStyle.FLORAL))
    }

    @Test
    fun testFunnyMood() {
        val text = "that is a hilarious joke lol haha funny!"
        val suggestion = MoodDetector.detectMood(text)
        assertEquals(KeyboardMood.FUNNY, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.contains(FrameStyle.PARTY))
    }

    @Test
    fun testExcitedMood() {
        val text = "wow omg awesome exciting victory"
        val suggestion = MoodDetector.detectMood(text)
        assertEquals(KeyboardMood.EXCITED, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.contains(FrameStyle.SPARKS))
    }

    @Test
    fun testLoveMood() {
        val text = "i love you so much xoxo sweet heart"
        val suggestion = MoodDetector.detectMood(text)
        assertEquals(KeyboardMood.LOVE, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.contains(FrameStyle.HEARTS))
    }

    @Test
    fun testCoolMood() {
        val text = "hey bro chill dope style cool obsidian"
        val suggestion = MoodDetector.detectMood(text)
        assertEquals(KeyboardMood.COOL, suggestion.mood)
        assertTrue(suggestion.suggestedStyles.contains(FrameStyle.GEM) || suggestion.suggestedStyles.contains(FrameStyle.DIAMOND))
    }
}
