package com.spelltype.keyboard

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import com.spelltype.keyboard.domain.usecase.ApplyFrameUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ApplyFrameUseCaseTest {

    private lateinit var fakeRepository: FakeKeyboardRepository
    private lateinit var applyFrameUseCase: ApplyFrameUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeKeyboardRepository()
        applyFrameUseCase = ApplyFrameUseCase(fakeRepository)
    }

    @Test
    fun testApplyFrameNoneStyle_doesNotSaveToDb() = runTest {
        val text = "No Frame"
        val result = applyFrameUseCase(text, FrameStyle.NONE)

        assertEquals("No Frame", result)
        val savedList = fakeRepository.getSavedArtList().first()
        assertTrue(savedList.isEmpty())
    }

    @Test
    fun testApplyFrameBoxStyle_savesToDb() = runTest {
        val text = "Frame Me"
        val result = applyFrameUseCase(text, FrameStyle.BOX)

        val expected = "┌──────────┐\n│ Frame Me │\n└──────────┘"
        assertEquals(expected, result)

        val savedList = fakeRepository.getSavedArtList().first()
        assertEquals(1, savedList.size)
        val savedItem = savedList[0]
        assertEquals("Frame Me", savedItem.originalText)
        assertEquals(expected, savedItem.styledText)
        assertEquals(FrameStyle.BOX.name, savedItem.styleName)
    }

    @Test
    fun testCombinedPipeline_appliesAllFeatures() = runTest {
        // Text: "A"
        // Unicode: CIRCLED ("Ⓐ")
        // Shape: PYRAMID ("Ⓐ")
        // Frame: STAR ("★★★★★\n★ Ⓐ ★\n★★★★★")
        // Glitter: True ("✨ ★★★★★ ✨\n✨ ★ Ⓐ ★ ✨\n✨ ★★★★★ ✨")
        // Signature: "- Me" ("✨ ★★★★★ ✨\n✨ ★ Ⓐ ★ ✨\n✨ ★★★★★ ✨\n- Me")

        val result = applyFrameUseCase(
            text = "A",
            style = FrameStyle.STAR,
            shape = ShapeLayout.PYRAMID,
            unicode = UnicodeStyle.CIRCLED,
            glitterEnabled = true,
            signature = "- Me"
        )

        assertTrue(result.contains("Ⓐ"))
        assertTrue(result.contains("★"))
        assertTrue(result.contains("✨"))
        assertTrue(result.endsWith("- Me"))

        val savedList = fakeRepository.getSavedArtList().first()
        assertEquals(1, savedList.size)
    }
}
