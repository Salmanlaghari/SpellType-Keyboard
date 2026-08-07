package com.salmanlaghari.spelltypekeyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
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

        // Phase 6 Premium Enhancements Settings
        val KEYBOARD_WALLPAPER_PATH = stringPreferencesKey("keyboard_wallpaper_path")
        val KEYBOARD_WALLPAPER_OPACITY = intPreferencesKey("keyboard_wallpaper_opacity")
        val KEY_SHAPE = stringPreferencesKey("key_shape")
        val KEY_BORDER_ENABLED = booleanPreferencesKey("key_border_enabled")
        val KEY_BORDER_THICKNESS = intPreferencesKey("key_border_thickness")
        val KEY_TEXT_SIZE = stringPreferencesKey("key_text_size")
    }

    /** Reads a stored value, falling back to [default] when it was never written. */
    private fun <T> preferenceFlow(key: Preferences.Key<T>, default: T): Flow<T> =
        context.dataStore.data.map { preferences -> preferences[key] ?: default }

    /** Reads an enum stored by name, falling back to [default] for missing or unknown names. */
    private fun <T : Enum<T>> enumFlow(
        key: Preferences.Key<String>,
        values: Array<T>,
        default: T
    ): Flow<T> = preferenceFlow(key, default.name).map { name ->
        values.firstOrNull { it.name == name } ?: default
    }

    private suspend fun <T> savePreference(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences -> preferences[key] = value }
    }

    val selectedFrameStyleFlow: Flow<FrameStyle> =
        enumFlow(SELECTED_FRAME_STYLE, FrameStyle.values(), FrameStyle.NONE)

    val selectedShapeLayoutFlow: Flow<ShapeLayout> =
        enumFlow(SELECTED_SHAPE_LAYOUT, ShapeLayout.values(), ShapeLayout.NONE)

    val selectedUnicodeStyleFlow: Flow<UnicodeStyle> =
        enumFlow(SELECTED_UNICODE_STYLE, UnicodeStyle.values(), UnicodeStyle.NONE)

    val glitterEnabledFlow: Flow<Boolean> = preferenceFlow(GLITTER_ENABLED, false)

    val customSignatureFlow: Flow<String> = preferenceFlow(CUSTOM_SIGNATURE, "")

    val favoriteStylesFlow: Flow<Set<String>> = preferenceFlow(FAVORITE_STYLES, emptySet())

    val vibrationEnabledFlow: Flow<Boolean> = preferenceFlow(VIBRATION_ENABLED, true)

    val soundEnabledFlow: Flow<Boolean> = preferenceFlow(SOUND_ENABLED, true)

    val themeSelectionFlow: Flow<String> = preferenceFlow(THEME_SELECTION, "DARK")

    val premiumUnlockedFlow: Flow<Boolean> = preferenceFlow(PREMIUM_UNLOCKED, false)

    // Phase 6 Flows
    val colorfulPreviewEnabledFlow: Flow<Boolean> = preferenceFlow(COLORFUL_PREVIEW_ENABLED, true)

    val giantWordsEnabledFlow: Flow<Boolean> = preferenceFlow(GIANT_WORDS_ENABLED, false)

    val keyboardHeightFlow: Flow<String> = preferenceFlow(KEYBOARD_HEIGHT, "MEDIUM")

    val vibrationStrengthFlow: Flow<Int> = preferenceFlow(VIBRATION_STRENGTH, 50)

    val keySoundVolumeFlow: Flow<Int> = preferenceFlow(KEY_SOUND_VOLUME, 50)

    val numberRowEnabledFlow: Flow<Boolean> = preferenceFlow(NUMBER_ROW_ENABLED, true)

    val autoSuggestionsEnabledFlow: Flow<Boolean> = preferenceFlow(AUTO_SUGGESTIONS_ENABLED, true)

    val swipeTypingEnabledFlow: Flow<Boolean> = preferenceFlow(SWIPE_TYPING_ENABLED, false)

    // Premium UI & Upgrades Flow Getters
    val keyboardWallpaperPathFlow: Flow<String> = preferenceFlow(KEYBOARD_WALLPAPER_PATH, "")

    val keyboardWallpaperOpacityFlow: Flow<Int> = preferenceFlow(KEYBOARD_WALLPAPER_OPACITY, 50)

    val keyShapeFlow: Flow<String> = preferenceFlow(KEY_SHAPE, "ROUNDED")

    val keyBorderEnabledFlow: Flow<Boolean> = preferenceFlow(KEY_BORDER_ENABLED, true)

    val keyBorderThicknessFlow: Flow<Int> = preferenceFlow(KEY_BORDER_THICKNESS, 1)

    val keyTextSizeFlow: Flow<String> = preferenceFlow(KEY_TEXT_SIZE, "MEDIUM")

    suspend fun saveSelectedFrameStyle(style: FrameStyle) = savePreference(SELECTED_FRAME_STYLE, style.name)

    suspend fun saveSelectedShapeLayout(shape: ShapeLayout) = savePreference(SELECTED_SHAPE_LAYOUT, shape.name)

    suspend fun saveSelectedUnicodeStyle(style: UnicodeStyle) = savePreference(SELECTED_UNICODE_STYLE, style.name)

    suspend fun saveGlitterEnabled(enabled: Boolean) = savePreference(GLITTER_ENABLED, enabled)

    suspend fun saveCustomSignature(signature: String) = savePreference(CUSTOM_SIGNATURE, signature)

    suspend fun saveFavoriteStyles(favorites: Set<String>) = savePreference(FAVORITE_STYLES, favorites)

    suspend fun saveVibrationEnabled(enabled: Boolean) = savePreference(VIBRATION_ENABLED, enabled)

    suspend fun saveSoundEnabled(enabled: Boolean) = savePreference(SOUND_ENABLED, enabled)

    suspend fun saveThemeSelection(theme: String) = savePreference(THEME_SELECTION, theme)

    suspend fun savePremiumUnlocked(unlocked: Boolean) = savePreference(PREMIUM_UNLOCKED, unlocked)

    // Phase 6 Setters
    suspend fun saveColorfulPreviewEnabled(enabled: Boolean) = savePreference(COLORFUL_PREVIEW_ENABLED, enabled)

    suspend fun saveGiantWordsEnabled(enabled: Boolean) = savePreference(GIANT_WORDS_ENABLED, enabled)

    suspend fun saveKeyboardHeight(height: String) = savePreference(KEYBOARD_HEIGHT, height)

    suspend fun saveVibrationStrength(strength: Int) = savePreference(VIBRATION_STRENGTH, strength)

    suspend fun saveKeySoundVolume(volume: Int) = savePreference(KEY_SOUND_VOLUME, volume)

    suspend fun saveNumberRowEnabled(enabled: Boolean) = savePreference(NUMBER_ROW_ENABLED, enabled)

    suspend fun saveAutoSuggestionsEnabled(enabled: Boolean) = savePreference(AUTO_SUGGESTIONS_ENABLED, enabled)

    suspend fun saveSwipeTypingEnabled(enabled: Boolean) = savePreference(SWIPE_TYPING_ENABLED, enabled)

    // Premium UI & Upgrades Setters
    suspend fun saveKeyboardWallpaperPath(path: String) = savePreference(KEYBOARD_WALLPAPER_PATH, path)

    suspend fun saveKeyboardWallpaperOpacity(opacity: Int) = savePreference(KEYBOARD_WALLPAPER_OPACITY, opacity)

    suspend fun saveKeyShape(shape: String) = savePreference(KEY_SHAPE, shape)

    suspend fun saveKeyBorderEnabled(enabled: Boolean) = savePreference(KEY_BORDER_ENABLED, enabled)

    suspend fun saveKeyBorderThickness(thickness: Int) = savePreference(KEY_BORDER_THICKNESS, thickness)

    suspend fun saveKeyTextSize(size: String) = savePreference(KEY_TEXT_SIZE, size)
}
