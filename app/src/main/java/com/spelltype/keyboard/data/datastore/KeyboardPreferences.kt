package com.spelltype.keyboard.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
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
    }

    val selectedFrameStyleFlow: Flow<FrameStyle> = context.dataStore.data
        .map { preferences ->
            val styleName = preferences[SELECTED_FRAME_STYLE] ?: FrameStyle.NONE.name
            try {
                FrameStyle.valueOf(styleName)
            } catch (e: IllegalArgumentException) {
                FrameStyle.NONE
            }
        }

    suspend fun saveSelectedFrameStyle(style: FrameStyle) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_FRAME_STYLE] = style.name
        }
    }
}
