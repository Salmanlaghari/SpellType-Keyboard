package com.spelltype.keyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.spelltype.keyboard.domain.model.FrameStyle
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
}
