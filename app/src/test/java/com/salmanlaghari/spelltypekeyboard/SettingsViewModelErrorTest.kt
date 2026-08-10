package com.salmanlaghari.spelltypekeyboard

import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.SavedArt
import com.salmanlaghari.spelltypekeyboard.domain.usecase.*
import com.salmanlaghari.spelltypekeyboard.presentation.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelErrorTest {

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
    fun failedSave_reportsErrorInsteadOfSwallowingIt() = runTest(testDispatcher) {
        val firstError = async { viewModel.errors.first() }
        runCurrent()

        fakeRepository.failWrites = true
        viewModel.selectFrameStyle(FrameStyle.STAR)
        testScheduler.advanceUntilIdle()

        assertEquals("Couldn't save frame style", firstError.await())
        assertEquals(FrameStyle.NONE, viewModel.selectedFrameStyle.value)
    }

    @Test
    fun failedDelete_reportsErrorAndKeepsViewModelUsable() = runTest(testDispatcher) {
        val firstError = async { viewModel.errors.first() }
        fakeRepository.saveArt(SavedArt(id = 1, originalText = "A", styledText = "A", styleName = "NONE"))
        testScheduler.advanceUntilIdle()

        fakeRepository.failWrites = true
        viewModel.deleteArt(viewModel.savedArtList.value[0])
        testScheduler.advanceUntilIdle()

        assertEquals("Couldn't save changes to your saved art", firstError.await())
        assertEquals(1, viewModel.savedArtList.value.size)

        // A later write still succeeds, so one failure doesn't tear down the scope.
        fakeRepository.failWrites = false
        viewModel.selectFrameStyle(FrameStyle.STAR)
        testScheduler.advanceUntilIdle()
        assertEquals(FrameStyle.STAR, viewModel.selectedFrameStyle.value)
    }
}
