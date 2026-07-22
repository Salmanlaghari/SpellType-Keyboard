package com.spelltype.keyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spelltype_settings")

class KeyboardPreferences(private val context: Context) {

    companion object {
        val SELECTED_FRAME_STYLE = stringPreferencesKey("selected_frame_style")
        val SELECTED_SHAPE_LAYOUT = stringPreferencesKey("selected_shape_layout")
        val SELECTED_UNICODE_STYLE = stringPreferencesKey("selected_unicode_style")
        val GLITTER_ENABLED = booleanPreferencesKey("glitter_enabled")
        val CUSTOM_SIGNATURE = stringPreferencesKey("custom_signature")
        val FAVORITE_STYLES = stringSetPreferencesKey("favorite_styles")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val THEME_SELECTION = stringPreferencesKey("theme_selection")
        val PREMIUM_UNLOCKED = booleanPreferencesKey("premium_unlocked")

        // Phase 6 Settings
        val COLORFUL_PREVIEW_ENABLED = booleanPreferencesKey("colorful_preview_enabled")
        val GIANT_WORDS_ENABLED = booleanPreferencesKey("giant_words_enabled")
        val KEYBOARD_HEIGHT = stringPreferencesKey("keyboard_height")
        val VIBRATION_STRENGTH = intPreferencesKey("vibration_strength")
        val KEY_SOUND_VOLUME = intPreferencesKey("key_sound_volume")
        val NUMBER_ROW_ENABLED = booleanPreferencesKey("number_row_enabled")
        val AUTO_SUGGESTIONS_ENABLED = booleanPreferencesKey("auto_suggestions_enabled")
        val SWIPE_TYPING_ENABLED = booleanPreferencesKey("swipe_typing_enabled")
    }

    val selectedFrameStyleFlow: Flow<FrameStyle> = context.dataStore.data
        .map { preferences ->
            val name = preferences[SELECTED_FRAME_STYLE] ?: FrameStyle.NONE.name
            try { FrameStyle.valueOf(name) } catch (e: Exception) { FrameStyle.NONE }
        }

    val selectedShapeLayoutFlow: Flow<ShapeLayout> = context.dataStore.data
        .map { preferences ->
            val name = preferences[SELECTED_SHAPE_LAYOUT] ?: ShapeLayout.NONE.name
            try { ShapeLayout.valueOf(name) } catch (e: Exception) { ShapeLayout.NONE }
        }

    val selectedUnicodeStyleFlow: Flow<UnicodeStyle> = context.dataStore.data
        .map { preferences ->
            val name = preferences[SELECTED_UNICODE_STYLE] ?: UnicodeStyle.NONE.name
            try { UnicodeStyle.valueOf(name) } catch (e: Exception) { UnicodeStyle.NONE }
        }

    val glitterEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[GLITTER_ENABLED] ?: false
        }

    val customSignatureFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CUSTOM_SIGNATURE] ?: ""
        }

    val favoriteStylesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITE_STYLES] ?: emptySet()
        }

    val vibrationEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[VIBRATION_ENABLED] ?: true
        }

    val soundEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SOUND_ENABLED] ?: true
        }

    val themeSelectionFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_SELECTION] ?: "DARK"
        }

    val premiumUnlockedFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PREMIUM_UNLOCKED] ?: false
        }

    // Phase 6 Flow Getters
    val colorfulPreviewEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COLORFUL_PREVIEW_ENABLED] ?: true
        }

    val giantWordsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[GIANT_WORDS_ENABLED] ?: false
        }

    val keyboardHeightFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEYBOARD_HEIGHT] ?: "MEDIUM"
        }

    val vibrationStrengthFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[VIBRATION_STRENGTH] ?: 50
        }

    val keySoundVolumeFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_SOUND_VOLUME] ?: 50
        }

    val numberRowEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NUMBER_ROW_ENABLED] ?: true
        }

    val autoSuggestionsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_SUGGESTIONS_ENABLED] ?: true
        }

    val swipeTypingEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SWIPE_TYPING_ENABLED] ?: false
        }

    suspend fun saveSelectedFrameStyle(style: FrameStyle) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_FRAME_STYLE] = style.name
        }
    }

    suspend fun saveSelectedShapeLayout(shape: ShapeLayout) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_SHAPE_LAYOUT] = shape.name
        }
    }

    suspend fun saveSelectedUnicodeStyle(style: UnicodeStyle) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_UNICODE_STYLE] = style.name
        }
    }

    suspend fun saveGlitterEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GLITTER_ENABLED] = enabled
        }
    }

    suspend fun saveCustomSignature(signature: String) {
        context.dataStore.edit { preferences ->
            preferences[CUSTOM_SIGNATURE] = signature
        }
    }

    suspend fun saveFavoriteStyles(favorites: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITE_STYLES] = favorites
        }
    }

    suspend fun saveVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun saveSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    suspend fun saveThemeSelection(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_SELECTION] = theme
        }
    }

    suspend fun savePremiumUnlocked(unlocked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PREMIUM_UNLOCKED] = unlocked
        }
    }

    // Phase 6 Flow Setters
    suspend fun saveColorfulPreviewEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COLORFUL_PREVIEW_ENABLED] = enabled
        }
    }

    suspend fun saveGiantWordsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GIANT_WORDS_ENABLED] = enabled
        }
    }

    suspend fun saveKeyboardHeight(height: String) {
        context.dataStore.edit { preferences ->
            preferences[KEYBOARD_HEIGHT] = height
        }
    }

    suspend fun saveVibrationStrength(strength: Int) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_STRENGTH] = strength
        }
    }

    suspend fun saveKeySoundVolume(volume: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SOUND_VOLUME] = volume
        }
    }

    suspend fun saveNumberRowEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NUMBER_ROW_ENABLED] = enabled
        }
    }

    suspend fun saveAutoSuggestionsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SUGGESTIONS_ENABLED] = enabled
        }
    }

    suspend fun saveSwipeTypingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SWIPE_TYPING_ENABLED] = enabled
        }
    }
}
