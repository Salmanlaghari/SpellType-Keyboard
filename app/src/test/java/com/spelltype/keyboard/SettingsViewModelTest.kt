package com.spelltype.keyboard

import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.SavedArt
import com.spelltype.keyboard.domain.usecase.*
import com.spelltype.keyboard.presentation.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeKeyboardRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeKeyboardRepository()
        viewModel = SettingsViewModel(
            repository = fakeRepository,
            getSavedArtListUseCase = GetSavedArtListUseCase(fakeRepository),
            deleteArtUseCase = DeleteArtUseCase(fakeRepository),
            getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(fakeRepository),
            saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() = runTest(testDispatcher) {
        // Collect StateFlow values
        val initialStyle = viewModel.selectedFrameStyle.value
        assertEquals(FrameStyle.NONE, initialStyle)

        val initialHistory = viewModel.savedArtList.value
        assertTrue(initialHistory.isEmpty())
    }

    @Test
    fun testSelectFrameStyle_updatesState() = runTest(testDispatcher) {
        viewModel.selectFrameStyle(FrameStyle.STAR)

        // Trigger execution of coroutine
        testScheduler.advanceUntilIdle()

        assertEquals(FrameStyle.STAR, viewModel.selectedFrameStyle.value)
    }

    @Test
    fun testDeleteSavedArt_removesFromHistory() = runTest(testDispatcher) {
        // Insert item directly in fake repo
        val artItem = SavedArt(id = 1, originalText = "Test", styledText = "[Test]", styleName = "BOX")
        fakeRepository.saveArt(artItem)
        testScheduler.advanceUntilIdle()

        // Verify it exists in view model flow
        assertEquals(1, viewModel.savedArtList.value.size)

        // Delete via view model
        viewModel.deleteArt(viewModel.savedArtList.value[0])
        testScheduler.advanceUntilIdle()

        // Verify history is empty
        assertTrue(viewModel.savedArtList.value.isEmpty())
    }

    @Test
    fun testClearAllHistory_clearsEverything() = runTest(testDispatcher) {
        fakeRepository.saveArt(SavedArt(id = 1, originalText = "A", styledText = "A", styleName = "NONE"))
        fakeRepository.saveArt(SavedArt(id = 2, originalText = "B", styledText = "B", styleName = "NONE"))
        testScheduler.advanceUntilIdle()

        assertEquals(2, viewModel.savedArtList.value.size)

        viewModel.clearAllArt()
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.savedArtList.value.isEmpty())
    }
}
