package com.salmanlaghari.spelltypekeyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.salmanlaghari.spelltypekeyboard.core.AppLog
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

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

        // Phase 6 Premium Enhancements Settings
        val KEYBOARD_WALLPAPER_PATH = stringPreferencesKey("keyboard_wallpaper_path")
        val KEYBOARD_WALLPAPER_OPACITY = intPreferencesKey("keyboard_wallpaper_opacity")
        val KEY_SHAPE = stringPreferencesKey("key_shape")
        val KEY_BORDER_ENABLED = booleanPreferencesKey("key_border_enabled")
        val KEY_BORDER_THICKNESS = intPreferencesKey("key_border_thickness")
        val KEY_TEXT_SIZE = stringPreferencesKey("key_text_size")
    }

    /**
     * Reads of an unreadable preferences file fall back to defaults instead of cancelling every
     * collector (which would take the keyboard service down with it). Anything else propagates.
     */
    private val preferencesFlow: Flow<Preferences> = context.dataStore.data
        .catch { error ->
            if (error is IOException) {
                AppLog.e("KeyboardPreferences.read", error)
                emit(emptyPreferences())
            } else {
                throw error
            }
        }

    private inline fun <reified T : Enum<T>> parseEnum(name: String, default: T): T {
        return try {
            enumValueOf<T>(name)
        } catch (e: IllegalArgumentException) {
            AppLog.e("KeyboardPreferences.parseEnum(${T::class.java.simpleName})", e)
            default
        }
    }

    val selectedFrameStyleFlow: Flow<FrameStyle> = preferencesFlow
        .map { preferences ->
            val name = preferences[SELECTED_FRAME_STYLE] ?: FrameStyle.NONE.name
            parseEnum(name, FrameStyle.NONE)
        }

    val selectedShapeLayoutFlow: Flow<ShapeLayout> = preferencesFlow
        .map { preferences ->
            val name = preferences[SELECTED_SHAPE_LAYOUT] ?: ShapeLayout.NONE.name
            parseEnum(name, ShapeLayout.NONE)
        }

    val selectedUnicodeStyleFlow: Flow<UnicodeStyle> = preferencesFlow
        .map { preferences ->
            val name = preferences[SELECTED_UNICODE_STYLE] ?: UnicodeStyle.NONE.name
            parseEnum(name, UnicodeStyle.NONE)
        }

    val glitterEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[GLITTER_ENABLED] ?: false
        }

    val customSignatureFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[CUSTOM_SIGNATURE] ?: ""
        }

    val favoriteStylesFlow: Flow<Set<String>> = preferencesFlow
        .map { preferences ->
            preferences[FAVORITE_STYLES] ?: emptySet()
        }

    val vibrationEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[VIBRATION_ENABLED] ?: true
        }

    val soundEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[SOUND_ENABLED] ?: true
        }

    val themeSelectionFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[THEME_SELECTION] ?: "DARK"
        }

    val premiumUnlockedFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[PREMIUM_UNLOCKED] ?: false
        }

    // Phase 6 Flows
    val colorfulPreviewEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[COLORFUL_PREVIEW_ENABLED] ?: true
        }

    val giantWordsEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[GIANT_WORDS_ENABLED] ?: false
        }

    val keyboardHeightFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[KEYBOARD_HEIGHT] ?: "MEDIUM"
        }

    val vibrationStrengthFlow: Flow<Int> = preferencesFlow
        .map { preferences ->
            preferences[VIBRATION_STRENGTH] ?: 50
        }

    val keySoundVolumeFlow: Flow<Int> = preferencesFlow
        .map { preferences ->
            preferences[KEY_SOUND_VOLUME] ?: 50
        }

    val numberRowEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[NUMBER_ROW_ENABLED] ?: true
        }

    val autoSuggestionsEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[AUTO_SUGGESTIONS_ENABLED] ?: true
        }

    val swipeTypingEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[SWIPE_TYPING_ENABLED] ?: false
        }

    // Premium UI & Upgrades Flow Getters
    val keyboardWallpaperPathFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[KEYBOARD_WALLPAPER_PATH] ?: ""
        }

    val keyboardWallpaperOpacityFlow: Flow<Int> = preferencesFlow
        .map { preferences ->
            preferences[KEYBOARD_WALLPAPER_OPACITY] ?: 50
        }

    val keyShapeFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[KEY_SHAPE] ?: "ROUNDED"
        }

    val keyBorderEnabledFlow: Flow<Boolean> = preferencesFlow
        .map { preferences ->
            preferences[KEY_BORDER_ENABLED] ?: true
        }

    val keyBorderThicknessFlow: Flow<Int> = preferencesFlow
        .map { preferences ->
            preferences[KEY_BORDER_THICKNESS] ?: 1
        }

    val keyTextSizeFlow: Flow<String> = preferencesFlow
        .map { preferences ->
            preferences[KEY_TEXT_SIZE] ?: "MEDIUM"
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

    // Phase 6 Setters
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

    // Premium UI & Upgrades Setters
    suspend fun saveKeyboardWallpaperPath(path: String) {
        context.dataStore.edit { preferences ->
            preferences[KEYBOARD_WALLPAPER_PATH] = path
        }
    }

    suspend fun saveKeyboardWallpaperOpacity(opacity: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEYBOARD_WALLPAPER_OPACITY] = opacity
        }
    }

    suspend fun saveKeyShape(shape: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHAPE] = shape
        }
    }

    suspend fun saveKeyBorderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BORDER_ENABLED] = enabled
        }
    }

    suspend fun saveKeyBorderThickness(thickness: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_BORDER_THICKNESS] = thickness
        }
    }

    suspend fun saveKeyTextSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TEXT_SIZE] = size
        }
    }
}
