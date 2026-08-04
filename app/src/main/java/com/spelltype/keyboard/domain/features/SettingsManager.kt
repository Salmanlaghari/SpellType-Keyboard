package com.spelltype.keyboard.domain.features

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager

/**
 * Real Settings Manager
 * Handles keyboard settings, input method selection, and system integration
 */
object SettingsManager {

    /**
     * Open keyboard settings in system
     */
    fun openKeyboardSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Open input method picker
     */
    fun openInputMethodPicker(context: Context) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Check if SpellType is the default keyboard
     */
    fun isDefaultKeyboard(context: Context): Boolean {
        try {
            val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            return defaultIme?.contains("spelltype", ignoreCase = true) == true
        } catch (e: Exception) {
            return false
        }
    }

    /**
     * Get current default keyboard name
     */
    fun getCurrentKeyboard(context: Context): String {
        try {
            val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
            return defaultIme ?: "Unknown"
        } catch (e: Exception) {
            return "Unknown"
        }
    }
}
