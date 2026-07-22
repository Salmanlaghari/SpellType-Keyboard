package com.spelltype.keyboard.presentation.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.TextView
import com.spelltype.keyboard.R
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import com.spelltype.keyboard.domain.usecase.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class SpellTypeIME : InputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var repository: KeyboardRepository
    private lateinit var applyFrameUseCase: ApplyFrameUseCase
    private lateinit var saveSelectedFrameStyleUseCase: SaveSelectedFrameStyleUseCase
    private lateinit var getSelectedFrameStyleUseCase: GetSelectedFrameStyleUseCase

    private var activeStyle = FrameStyle.NONE
    private var isShifted = false
    private var isSymbolMode = false

    private val composingText = StringBuilder()

    // Key views mapping for easy letter updates
    private val letterKeyIds = listOf(
        R.id.btn_q, R.id.btn_w, R.id.btn_e, R.id.btn_r, R.id.btn_t,
        R.id.btn_y, R.id.btn_u, R.id.btn_i, R.id.btn_o, R.id.btn_p,
        R.id.btn_a, R.id.btn_s, R.id.btn_d, R.id.btn_f, R.id.btn_g,
        R.id.btn_h, R.id.btn_j, R.id.btn_k, R.id.btn_l,
        R.id.btn_z, R.id.btn_x, R.id.btn_c, R.id.btn_v,
        R.id.btn_b, R.id.btn_n, R.id.btn_m
    )

    private val lettersLower = listOf(
        "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
        "a", "s", "d", "f", "g", "h", "j", "k", "l",
        "z", "x", "c", "v", "b", "n", "m"
    )

    private val lettersUpper = listOf(
        "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
        "A", "S", "D", "F", "G", "H", "J", "K", "L",
        "Z", "X", "C", "V", "B", "N", "M"
    )

    private val symbols = listOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
        "@", "#", "$", "%", "&", "-", "+", "(", ")",
        "*", "\"", "'", ":", ";", "!", "?"
    )

    private var keyViews = mutableMapOf<Int, TextView>()
    private var keyboardRootView: View? = null
    private var chipNone: TextView? = null
    private var chipBox: TextView? = null
    private var chipStar: TextView? = null
    private var chipBracket: TextView? = null
    private var chipDiamond: TextView? = null

    override fun onCreate() {
        super.onCreate()
        val database = SpellTypeDatabase.getDatabase(this)
        val preferences = KeyboardPreferences(this)
        repository = KeyboardRepositoryImpl(database.savedArtDao(), preferences)

        applyFrameUseCase = ApplyFrameUseCase(repository)
        saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(repository)
        getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(repository)
    }

    override fun onCreateInputView(): View {
        val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
        keyboardRootView = keyboardView

        // Initialize frame style chips
        chipNone = keyboardView.findViewById(R.id.chip_none)
        chipBox = keyboardView.findViewById(R.id.chip_box)
        chipStar = keyboardView.findViewById(R.id.chip_star)
        chipBracket = keyboardView.findViewById(R.id.chip_bracket)
        chipDiamond = keyboardView.findViewById(R.id.chip_diamond)

        // Register click listeners for style chips
        chipNone?.setOnClickListener { selectFrameStyle(FrameStyle.NONE) }
        chipBox?.setOnClickListener { selectFrameStyle(FrameStyle.BOX) }
        chipStar?.setOnClickListener { selectFrameStyle(FrameStyle.STAR) }
        chipBracket?.setOnClickListener { selectFrameStyle(FrameStyle.BRACKET) }
        chipDiamond?.setOnClickListener { selectFrameStyle(FrameStyle.DIAMOND) }

        // Find and map letter keys
        for (id in letterKeyIds) {
            val keyView: TextView = keyboardView.findViewById(id)
            keyViews[id] = keyView
            keyView.setOnClickListener { handleKeyClick(keyView.text.toString()) }
        }

        // Special keys
        keyboardView.findViewById<View>(R.id.btn_shift).setOnClickListener { toggleShift() }
        keyboardView.findViewById<View>(R.id.btn_backspace).setOnClickListener { handleBackspace() }
        keyboardView.findViewById<View>(R.id.btn_mode).setOnClickListener { toggleMode() }
        keyboardView.findViewById<View>(R.id.btn_space).setOnClickListener { handleSpace() }
        keyboardView.findViewById<View>(R.id.btn_enter).setOnClickListener { handleEnter() }

        // Load initially saved style
        serviceScope.launch {
            getSelectedFrameStyleUseCase().collect { style ->
                activeStyle = style
                updateChipHighlighting()
            }
        }

        updateKeyLabels()
        return keyboardView
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun selectFrameStyle(style: FrameStyle) {
        serviceScope.launch {
            if (composingText.isNotEmpty()) {
                commitComposingText()
            }
            saveSelectedFrameStyleUseCase(style)
            activeStyle = style
            updateChipHighlighting()
        }
    }

    private fun updateChipHighlighting() {
        chipNone?.setBackgroundResource(if (activeStyle == FrameStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        chipBox?.setBackgroundResource(if (activeStyle == FrameStyle.BOX) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        chipStar?.setBackgroundResource(if (activeStyle == FrameStyle.STAR) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        chipBracket?.setBackgroundResource(if (activeStyle == FrameStyle.BRACKET) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        chipDiamond?.setBackgroundResource(if (activeStyle == FrameStyle.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun handleKeyClick(text: String) {
        val ic: InputConnection = currentInputConnection ?: return
        if (activeStyle == FrameStyle.NONE) {
            ic.commitText(text, 1)
        } else {
            composingText.append(text)
            ic.setComposingText(composingText.toString(), 1)
        }

        // Auto reset Shift if it was standard shifted
        if (isShifted && !isSymbolMode) {
            isShifted = false
            updateKeyLabels()
        }
    }

    private fun toggleShift() {
        if (isSymbolMode) return
        isShifted = !isShifted
        updateKeyLabels()
    }

    private fun toggleMode() {
        isSymbolMode = !isSymbolMode
        isShifted = false
        updateKeyLabels()
    }

    private fun updateKeyLabels() {
        val modeButton: TextView? = keyboardRootView?.findViewById(R.id.btn_mode)
        modeButton?.text = if (isSymbolMode) "abc" else "?123"

        for (i in letterKeyIds.indices) {
            val id = letterKeyIds[i]
            val view = keyViews[id] ?: continue

            if (isSymbolMode) {
                // Symbols mode mapping
                if (i < symbols.size) {
                    view.text = symbols[i]
                    view.visibility = View.VISIBLE
                } else {
                    view.visibility = View.INVISIBLE
                }
            } else {
                // Normal letters mode mapping
                view.visibility = View.VISIBLE
                view.text = if (isShifted) lettersUpper[i] else lettersLower[i]
            }
        }
    }

    private fun handleBackspace() {
        val ic: InputConnection = currentInputConnection ?: return
        if (activeStyle != FrameStyle.NONE && composingText.isNotEmpty()) {
            composingText.deleteAt(composingText.length - 1)
            if (composingText.isEmpty()) {
                ic.commitText("", 1)
            } else {
                ic.setComposingText(composingText.toString(), 1)
            }
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun handleSpace() {
        val ic: InputConnection = currentInputConnection ?: return
        if (activeStyle != FrameStyle.NONE && composingText.isNotEmpty()) {
            commitComposingText {
                ic.commitText(" ", 1)
            }
        } else {
            ic.commitText(" ", 1)
        }
    }

    private fun handleEnter() {
        val ic: InputConnection = currentInputConnection ?: return
        if (activeStyle != FrameStyle.NONE && composingText.isNotEmpty()) {
            commitComposingText {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        } else {
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
        }
    }

    private fun commitComposingText(onComplete: (() -> Unit)? = null) {
        val textToFormat = composingText.toString()
        composingText.clear()
        val ic: InputConnection = currentInputConnection ?: return

        serviceScope.launch {
            val styled = applyFrameUseCase(textToFormat, activeStyle)
            ic.commitText(styled, 1)
            onComplete?.invoke()
        }
    }
}
