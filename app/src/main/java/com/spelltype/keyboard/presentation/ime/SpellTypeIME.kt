package com.spelltype.keyboard.presentation.ime

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import com.spelltype.keyboard.R
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.ShapeEngine
import com.spelltype.keyboard.domain.StyleCategorizer
import com.spelltype.keyboard.domain.UnicodeStylingEngine
import com.spelltype.keyboard.domain.PreviewStyler
import com.spelltype.keyboard.domain.MoodDetector
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import com.spelltype.keyboard.domain.repository.KeyboardRepository
import com.spelltype.keyboard.domain.usecase.*
import kotlinx.coroutines.*

class SpellTypeIME : InputMethodService() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var repository: KeyboardRepository? = null
    private var applyFrameUseCase: ApplyFrameUseCase? = null
    private var saveSelectedFrameStyleUseCase: SaveSelectedFrameStyleUseCase? = null
    private var getSelectedFrameStyleUseCase: GetSelectedFrameStyleUseCase? = null

    private var activeStyle = FrameStyle.NONE
    private var activeShape = ShapeLayout.NONE
    private var activeUnicode = UnicodeStyle.NONE
    private var glitterEnabled = false
    private var customSignature = ""
    private var favoriteStyles = emptySet<String>()

    // Phase 5 & 6 Settings with strict defaults
    private var vibrationEnabled = true
    private var soundEnabled = true
    private var vibrationStrength = 50
    private var soundVolume = 50
    private var keyboardHeight = "MEDIUM"
    private var numberRowEnabled = true
    private var autoSuggestionsEnabled = true
    private var colorfulPreviewEnabled = true
    private var giantWordsEnabled = false
    private var themeSelection = "DARK"

    // Premium Configurations
    private var keyboardWallpaperPath = ""
    private var keyboardWallpaperOpacity = 50
    private var keyShape = "ROUNDED"
    private var keyBorderEnabled = true
    private var keyBorderThickness = 1
    private var keyTextSize = "MEDIUM"
    private var premiumUnlocked = false

    private var isShifted = false
    private var isSymbolMode = false

    private val composingText = StringBuilder()

    private val letterKeyIds = listOf(
        R.id.btn_q, R.id.btn_w, R.id.btn_e, R.id.btn_r, R.id.btn_t,
        R.id.btn_y, R.id.btn_u, R.id.btn_i, R.id.btn_o, R.id.btn_p,
        R.id.btn_a, R.id.btn_s, R.id.btn_d, R.id.btn_f, R.id.btn_g,
        R.id.btn_h, R.id.btn_j, R.id.btn_k, R.id.btn_l,
        R.id.btn_z, R.id.btn_x, R.id.btn_c, R.id.btn_v,
        R.id.btn_b, R.id.btn_n, R.id.btn_m
    )

    private val numKeyIds = listOf(
        R.id.num_1, R.id.num_2, R.id.num_3, R.id.num_4, R.id.num_5,
        R.id.num_6, R.id.num_7, R.id.num_8, R.id.num_9, R.id.num_0
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

    private val emojis3D = listOf(
        "🔥", "💖", "✨", "⭐", "🌟", "💫", "🎉", "🎈", "👑", "💎",
        "🍀", "🌸", "🎵", "❄️", "🍁", "⚔️", "💀", "☕", "🐟", "👻",
        "👽", "🧸", "🍭", "🎁", "🚀", "🛸", "🎮", "🍕", "🥑", "🦁",
        "🦄", "🌈", "🌍", "⚡", "🔮", "🧬", "🧸", "🦾", "🧿", "🎨",
        "🎸", "🛹", "🍿", "🍩", "🍦", "🧁", "🍹", "🍷", "🔔", "📿",
        "🥺", "🥰", "🥶", "🥳", "🤠", "🤖"
    )

    private var isEmojiMode = false

    private var keyViews = mutableMapOf<Int, TextView>()
    private var keyboardRootView: View? = null

    // Suggestions Style Targets
    private var suggestedStyleLeft: FrameStyle? = null
    private var suggestedStyleRight: FrameStyle? = null

    override fun onCreate() {
        super.onCreate()
        try {
            // Safer context leak preventions
            val contextToUse = applicationContext ?: this
            val database = SpellTypeDatabase.getDatabase(contextToUse)
            val preferences = KeyboardPreferences(contextToUse)
            val repo = KeyboardRepositoryImpl(database.savedArtDao(), preferences)
            repository = repo

            applyFrameUseCase = ApplyFrameUseCase(repo)
            saveSelectedFrameStyleUseCase = SaveSelectedFrameStyleUseCase(repo)
            getSelectedFrameStyleUseCase = GetSelectedFrameStyleUseCase(repo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyboardAdBanners() {
        try {
            val root = keyboardRootView ?: return
            val topContainer = root.findViewById<android.widget.FrameLayout>(R.id.keyboard_top_ad_container)
            val bottomContainer = root.findViewById<android.widget.FrameLayout>(R.id.keyboard_bottom_ad_container)

            if (premiumUnlocked) {
                topContainer?.visibility = View.GONE
                bottomContainer?.visibility = View.GONE
            } else {
                // Initialize AdMob SDK first
                com.spelltype.keyboard.presentation.ads.AdManager.init(applicationContext ?: this)

                // Load Top Banner
                topContainer?.let { container ->
                    container.visibility = View.VISIBLE
                    com.spelltype.keyboard.presentation.ads.AdManager.loadBanner(
                        context = this,
                        type = com.spelltype.keyboard.presentation.ads.BannerType.KEYBOARD_TOP,
                        adSize = com.google.android.gms.ads.AdSize.BANNER
                    ) { adView ->
                        container.removeAllViews()
                        container.addView(adView)
                    }
                }

                // Load Bottom Banner
                bottomContainer?.let { container ->
                    container.visibility = View.VISIBLE
                    com.spelltype.keyboard.presentation.ads.AdManager.loadBanner(
                        context = this,
                        type = com.spelltype.keyboard.presentation.ads.BannerType.KEYBOARD_BOTTOM,
                        adSize = com.google.android.gms.ads.AdSize.BANNER
                    ) { adView ->
                        container.removeAllViews()
                        container.addView(adView)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        try {
            composingText.clear()
            updateLivePreviewBar()
            updateSuggestionsBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateInputView(): View {
        try {
            val keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null)
            keyboardRootView = keyboardView

            // Map standard and special letter keys
            for (id in letterKeyIds) {
                val keyView: TextView? = keyboardView.findViewById(id)
                if (keyView != null) {
                    keyViews[id] = keyView
                    keyView.setOnClickListener {
                        onKeyClickFeedback(keyView)
                        handleKeyClick(keyView.text.toString())
                    }
                }
            }

            // Map permanent number row keys
            for (id in numKeyIds) {
                val numView: TextView? = keyboardView.findViewById(id)
                numView?.setOnClickListener {
                    onKeyClickFeedback(numView)
                    handleKeyClick(numView.text.toString())
                }
            }

            // Special keys with safety
            val btnShift = keyboardView.findViewById<View>(R.id.btn_shift)
            btnShift?.setOnClickListener {
                onKeyClickFeedback(btnShift)
                toggleShift()
            }

            val btnBackspace = keyboardView.findViewById<View>(R.id.btn_backspace)
            btnBackspace?.setOnClickListener {
                onKeyClickFeedback(btnBackspace)
                handleBackspace()
            }

            val btnMode = keyboardView.findViewById<View>(R.id.btn_mode)
            btnMode?.setOnClickListener {
                onKeyClickFeedback(btnMode)
                toggleMode()
            }

            val btnSpace = keyboardView.findViewById<View>(R.id.btn_space)
            btnSpace?.setOnClickListener {
                onKeyClickFeedback(btnSpace)
                handleSpace()
            }

            val btnEnter = keyboardView.findViewById<View>(R.id.btn_enter)
            btnEnter?.setOnClickListener {
                onKeyClickFeedback(btnEnter)
                handleEnter()
            }

            // Trigger Ad Banner loading
            updateKeyboardAdBanners()

            // Pro Tools Click Listeners
            keyboardView.findViewById<View>(R.id.tool_clipboard)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleClipboardToolWithAd()
            }

            keyboardView.findViewById<View>(R.id.tool_translate)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleTranslateToolWithAd()
            }

            keyboardView.findViewById<View>(R.id.tool_templates)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleTemplatesToolWithAd()
            }

            // Setup suggestions click listeners
            keyboardView.findViewById<View>(R.id.suggestion_left)?.setOnClickListener {
                suggestedStyleLeft?.let { style ->
                    onKeyClickFeedback(it)
                    selectFrameStyle(style)
                }
            }
            keyboardView.findViewById<View>(R.id.suggestion_right)?.setOnClickListener {
                suggestedStyleRight?.let { style ->
                    onKeyClickFeedback(it)
                    selectFrameStyle(style)
                }
            }

            // Load dynamic settings flow safely
            val repo = repository
            val getStyleUseCase = getSelectedFrameStyleUseCase
            if (repo != null) {
                if (getStyleUseCase != null) {
                    serviceScope.launch {
                        getStyleUseCase().collect { style ->
                            activeStyle = style
                            refreshQuickArtBar()
                            updateLivePreviewBar()
                        }
                    }
                }
                serviceScope.launch {
                    repo.getSelectedShapeLayout().collect { shape ->
                        activeShape = shape
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getSelectedUnicodeStyle().collect { unicode ->
                        activeUnicode = unicode
                        refreshQuickArtBar()
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getGlitterEnabled().collect { enabled ->
                        glitterEnabled = enabled
                        refreshQuickArtBar()
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getCustomSignature().collect { signature ->
                        customSignature = signature
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getFavoriteStyles().collect { favorites ->
                        favoriteStyles = favorites
                        refreshQuickArtBar()
                    }
                }
                serviceScope.launch {
                    repo.getVibrationEnabled().collect { enabled ->
                        vibrationEnabled = enabled
                    }
                }
                serviceScope.launch {
                    repo.getSoundEnabled().collect { enabled ->
                        soundEnabled = enabled
                    }
                }
                serviceScope.launch {
                    repo.getVibrationStrength().collect { strength ->
                        vibrationStrength = strength
                    }
                }
                serviceScope.launch {
                    repo.getKeySoundVolume().collect { volume ->
                        soundVolume = volume
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardHeight().collect { height ->
                        keyboardHeight = height
                        applyKeyboardHeight(height)
                    }
                }
                serviceScope.launch {
                    repo.getNumberRowEnabled().collect { enabled ->
                        numberRowEnabled = enabled
                        keyboardRootView?.findViewById<View>(R.id.number_row)?.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }
                serviceScope.launch {
                    repo.getAutoSuggestionsEnabled().collect { enabled ->
                        autoSuggestionsEnabled = enabled
                        keyboardRootView?.findViewById<View>(R.id.auto_suggestions_bar)?.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }
                serviceScope.launch {
                    repo.getColorfulPreviewEnabled().collect { enabled ->
                        colorfulPreviewEnabled = enabled
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getGiantWordsEnabled().collect { enabled ->
                        giantWordsEnabled = enabled
                        updateLivePreviewBar()
                    }
                }
                serviceScope.launch {
                    repo.getThemeSelection().collect { theme ->
                        themeSelection = theme
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardWallpaperPath().collect { path ->
                        keyboardWallpaperPath = path
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyboardWallpaperOpacity().collect { opacity ->
                        keyboardWallpaperOpacity = opacity
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyShape().collect { shape ->
                        keyShape = shape
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyBorderEnabled().collect { enabled ->
                        keyBorderEnabled = enabled
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyBorderThickness().collect { thickness ->
                        keyBorderThickness = thickness
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getKeyTextSize().collect { size ->
                        keyTextSize = size
                        applyCustomConfigurations()
                    }
                }
                serviceScope.launch {
                    repo.getPremiumUnlocked().collect { unlocked ->
                        premiumUnlocked = unlocked
                        updateKeyboardAdBanners()
                    }
                }
            }

            // Populate Quick Art Bar safely
            val container = keyboardView.findViewById<LinearLayout>(R.id.quick_art_container)
            if (container != null) {
                populateQuickArtBar(container)
            }

            updateKeyLabels()
            return keyboardView
        } catch (e: Exception) {
            e.printStackTrace()
            // Never let the keyboard crash. Always return a dummy/fallback view to maintain system stability!
            val fallback = View(applicationContext ?: this)
            fallback.minimumHeight = 1
            return fallback
        }
    }

    override fun onDestroy() {
        try {
            serviceJob.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    private fun getSortedStyles(): List<FrameStyle> {
        val allStyles = FrameStyle.values().toList()
        return allStyles.sortedWith(compareBy(
            { it != FrameStyle.NONE },
            { !favoriteStyles.contains(it.name) }
        ))
    }

    private fun populateQuickArtBar(container: LinearLayout) {
        try {
            container.removeAllViews()

            val density = resources.displayMetrics.density
            val padLR = (12 * density).toInt()
            val padTB = (6 * density).toInt()
            val margin = (4 * density).toInt()

            // 1. Add 3D Emoji Toggle Chip
            val emojiChip = TextView(this)
            emojiChip.text = "😎 3D Emoji"
            emojiChip.setTextColor(resources.getColor(R.color.key_text_color, null))
            emojiChip.textSize = 12f
            emojiChip.setPadding(padLR, padTB, padLR, padTB)
            val emojiParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            emojiParams.setMargins(margin, 0, margin, 0)
            emojiChip.layoutParams = emojiParams
            emojiChip.isClickable = true
            emojiChip.isFocusable = true
            emojiChip.setBackgroundResource(
                if (isEmojiMode) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )
            emojiChip.setOnClickListener {
                onKeyClickFeedback(emojiChip)
                toggleEmojiMode()
                refreshQuickArtBar()
            }
            container.addView(emojiChip)

            // 2. Add Glitter Toggle Chip
            val glitterChip = TextView(this)
            glitterChip.text = if (glitterEnabled) "✨ Glitter: ON" else "✨ Glitter: OFF"
            glitterChip.setTextColor(resources.getColor(R.color.key_text_color, null))
            glitterChip.textSize = 12f
            glitterChip.setPadding(padLR, padTB, padLR, padTB)
            val glitterParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            glitterParams.setMargins(margin, 0, margin, 0)
            glitterChip.layoutParams = glitterParams
            glitterChip.isClickable = true
            glitterChip.isFocusable = true
            glitterChip.setBackgroundResource(
                if (glitterEnabled) R.drawable.chip_active_background
                else R.drawable.chip_inactive_background
            )
            glitterChip.setOnClickListener {
                onKeyClickFeedback(glitterChip)
                toggleGlitter()
            }
            container.addView(glitterChip)

            // 3. Add standard styles
            val styles = getSortedStyles()
            for (style in styles) {
                val textView = TextView(this)

                val isFav = favoriteStyles.contains(style.name)
                val isPrem = StyleCategorizer.isPremium(style)
                val prefix = when {
                    isFav -> "♥ "
                    isPrem -> "👑 "
                    else -> ""
                }

                val name = if (style == FrameStyle.NONE) "Normal" else style.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }
                textView.text = "$prefix$name"
                textView.setTextColor(resources.getColor(R.color.key_text_color, null))
                textView.textSize = 12f

                textView.setPadding(padLR, padTB, padLR, padTB)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(margin, 0, margin, 0)
                textView.layoutParams = params

                textView.isClickable = true
                textView.isFocusable = true
                textView.setBackgroundResource(
                    if (activeStyle == style && !isEmojiMode) R.drawable.chip_active_background
                    else R.drawable.chip_inactive_background
                )

                textView.setOnClickListener {
                    if (isEmojiMode) {
                        isEmojiMode = false
                        updateKeyLabels()
                    }
                    selectFrameStyle(style)
                }
                container.addView(textView)
            }

            // 4. Add Unicode Font Style Chips (excluding NONE)
            val unicodeStyles = UnicodeStyle.values().filter { it != UnicodeStyle.NONE }
            for (uStyle in unicodeStyles) {
                val textView = TextView(this)
                val name = uStyle.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }

                textView.text = "𝔽 $name"
                textView.setTextColor(resources.getColor(R.color.key_text_color, null))
                textView.textSize = 12f
                textView.setPadding(padLR, padTB, padLR, padTB)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(margin, 0, margin, 0)
                textView.layoutParams = params
                textView.isClickable = true
                textView.isFocusable = true
                textView.setBackgroundResource(
                    if (activeUnicode == uStyle && !isEmojiMode) R.drawable.chip_active_background
                    else R.drawable.chip_inactive_background
                )

                textView.setOnClickListener {
                    if (isEmojiMode) {
                        isEmojiMode = false
                        updateKeyLabels()
                    }
                    selectUnicodeStyle(uStyle)
                }
                container.addView(textView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val glowingKeys = mutableSetOf<Int>()

    private fun onKeyClickFeedback(view: View) {
        try {
            if (vibrationEnabled) {
                val vibrator = getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                vibrator?.let {
                    val duration = (vibrationStrength * 0.4).toLong().coerceAtLeast(1)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val amplitude = (vibrationStrength * 2.55).toInt().coerceIn(1, 255)
                        it.vibrate(android.os.VibrationEffect.createOneShot(duration, amplitude))
                    } else {
                        @Suppress("DEPRECATION")
                        it.vibrate(duration)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (soundEnabled) {
                val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
                val vol = soundVolume / 100f
                am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, vol)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Neon Glow click lighting effect + 3D tactile pressed squish
            val viewId = view.id
            if (view is TextView && (letterKeyIds.contains(viewId) || numKeyIds.contains(viewId) ||
                viewId == R.id.btn_shift || viewId == R.id.btn_backspace || viewId == R.id.btn_mode || viewId == R.id.btn_space || viewId == R.id.btn_enter)) {

                glowingKeys.add(viewId)
                applyKeyStyle(view, isPressed = true)

                serviceScope.launch {
                    delay(150)
                    glowingKeys.remove(viewId)
                    applyKeyStyle(view, isPressed = false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            // Smooth 120fps animation
            view.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(60)
                .withEndAction {
                    view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(60)
                        .start()
                }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectFrameStyle(style: FrameStyle) {
        val saveStyleUseCase = saveSelectedFrameStyleUseCase
        if (saveStyleUseCase != null) {
            serviceScope.launch {
                try {
                    saveStyleUseCase(style)
                    activeStyle = style
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun selectUnicodeStyle(style: UnicodeStyle) {
        val repo = repository
        if (repo != null) {
            serviceScope.launch {
                try {
                    repo.saveSelectedUnicodeStyle(style)
                    activeUnicode = style
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun toggleGlitter() {
        val repo = repository
        if (repo != null) {
            serviceScope.launch {
                try {
                    repo.saveGlitterEnabled(!glitterEnabled)
                    glitterEnabled = !glitterEnabled
                    refreshQuickArtBar()
                    updateLivePreviewBar()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun refreshQuickArtBar() {
        try {
            val container = keyboardRootView?.findViewById<LinearLayout>(R.id.quick_art_container) ?: return
            populateQuickArtBar(container)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isStylingActive(): Boolean {
        return activeStyle != FrameStyle.NONE ||
                activeShape != ShapeLayout.NONE ||
                activeUnicode != UnicodeStyle.NONE ||
                glitterEnabled
    }

    private fun handleKeyClick(text: String) {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (!isStylingActive()) {
                ic.commitText(text, 1)
            } else {
                composingText.append(text)
                ic.setComposingText(composingText.toString(), 1)
                updateLivePreviewBar()
                updateSuggestionsBar()
            }

            if (isShifted && !isSymbolMode) {
                isShifted = false
                updateKeyLabels()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleShift() {
        try {
            if (isSymbolMode || isEmojiMode) return
            isShifted = !isShifted
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleMode() {
        try {
            if (isEmojiMode) {
                isEmojiMode = false
                isSymbolMode = false
            } else {
                isSymbolMode = !isSymbolMode
                isEmojiMode = false
            }
            isShifted = false
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun toggleEmojiMode() {
        try {
            isEmojiMode = !isEmojiMode
            isSymbolMode = false
            isShifted = false
            updateKeyLabels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyLabels() {
        try {
            val modeButton: TextView? = keyboardRootView?.findViewById(R.id.btn_mode)
            modeButton?.text = when {
                isEmojiMode -> "abc"
                isSymbolMode -> "abc"
                else -> "?123"
            }

            for (i in letterKeyIds.indices) {
                val id = letterKeyIds[i]
                val view = keyViews[id] ?: continue

                when {
                    isEmojiMode -> {
                        if (i < emojis3D.size) {
                            view.text = emojis3D[i]
                            view.visibility = View.VISIBLE
                        } else {
                            view.visibility = View.INVISIBLE
                        }
                    }
                    isSymbolMode -> {
                        if (i < symbols.size) {
                            view.text = symbols[i]
                            view.visibility = View.VISIBLE
                        } else {
                            view.visibility = View.INVISIBLE
                        }
                    }
                    else -> {
                        view.visibility = View.VISIBLE
                        view.text = if (isShifted) lettersUpper[i] else lettersLower[i]
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleBackspace() {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (isStylingActive() && composingText.isNotEmpty()) {
                composingText.deleteAt(composingText.length - 1)
                if (composingText.isEmpty()) {
                    ic.commitText("", 1)
                } else {
                    ic.setComposingText(composingText.toString(), 1)
                }
                updateLivePreviewBar()
                updateSuggestionsBar()
            } else {
                ic.deleteSurroundingText(1, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleSpace() {
        try {
            val ic: InputConnection = currentInputConnection ?: return

            // Smart Text Expander (Shortcut Expander Ease Function)
            if (composingText.isNotEmpty()) {
                val text = composingText.toString().trim()
                val lower = text.lowercase()
                val shortcuts = mapOf(
                    "brb" to "Be Right Back 🏃‍♂️",
                    "hru" to "How are you? 🤔",
                    "omg" to "Oh My God! 😱",
                    "np" to "No Problem 👍",
                    "ty" to "Thank You So Much! ❤️",
                    "lol" to "Laughing Out Loud! 😂",
                    "g2g" to "Got To Go! 👋",
                    "idk" to "I Don't Know 🤷‍♂️",
                    "btw" to "By The Way 📌"
                )
                if (shortcuts.containsKey(lower)) {
                    composingText.clear()
                    composingText.append(shortcuts[lower])
                    updateLivePreviewBar()
                }
            }

            if (isStylingActive() && composingText.isNotEmpty()) {
                commitComposingText {
                    ic.commitText(" ", 1)
                }
            } else {
                ic.commitText(" ", 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClipboardTool() {
        try {
            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
            if (clipText.isNotEmpty()) {
                composingText.clear()
                composingText.append(clipText)
                updateLivePreviewBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClipboardToolWithAd() {
        if (premiumUnlocked) {
            handleClipboardTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleClipboardTool()
                },
                onFailed = {
                    handleClipboardTool()
                }
            )
        }
    }

    private fun handleTranslateTool() {
        try {
            val raw = composingText.toString()
            if (raw.isNotEmpty()) {
                // Cyber translator leet mock converter
                val translated = raw.lowercase()
                    .replace("a", "@")
                    .replace("e", "3")
                    .replace("i", "1")
                    .replace("o", "0")
                    .replace("s", "$")
                    .replace("t", "7")
                composingText.clear()
                composingText.append(translated)
                updateLivePreviewBar()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTranslateToolWithAd() {
        if (premiumUnlocked) {
            handleTranslateTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleTranslateTool()
                },
                onFailed = {
                    handleTranslateTool()
                }
            )
        }
    }

    private fun handleTemplatesTool() {
        try {
            val templates = listOf(
                "★ S P E L L T Y P E ★",
                "꧁𓊈𒆜 ⓈⓅⒺⓁⓁⓉⓎⓅⒺ 𒆜𓊉꧂",
                "┌────── ∘°❉°∘ ──────┐\n   WELCOME TO MY BIO\n└────── °∘❉∘° ──────┘"
            )
            val index = (0..2).random()
            composingText.clear()
            composingText.append(templates[index])
            updateLivePreviewBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTemplatesToolWithAd() {
        if (premiumUnlocked) {
            handleTemplatesTool()
        } else {
            com.spelltype.keyboard.presentation.ads.AdManager.loadInterstitial(
                context = this,
                type = com.spelltype.keyboard.presentation.ads.InterstitialType.PRO_TOOLS,
                onLoaded = { _ ->
                    // Service context safety fallback
                    handleTemplatesTool()
                },
                onFailed = {
                    handleTemplatesTool()
                }
            )
        }
    }

    private fun handleEnter() {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (isStylingActive() && composingText.isNotEmpty()) {
                commitComposingText {
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                    ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
                }
            } else {
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun commitComposingText(onComplete: (() -> Unit)? = null) {
        try {
            val textToFormat = composingText.toString()
            composingText.clear()
            updateLivePreviewBar()
            updateSuggestionsBar()
            val ic: InputConnection = currentInputConnection ?: return
            val formatUseCase = applyFrameUseCase
            if (formatUseCase != null) {
                serviceScope.launch {
                    try {
                        val styled = formatUseCase(
                            text = textToFormat,
                            style = activeStyle,
                            shape = activeShape,
                            unicode = activeUnicode,
                            glitterEnabled = glitterEnabled,
                            signature = customSignature
                        )
                        ic.commitText(styled, 1)
                        onComplete?.invoke()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateLivePreviewBar() {
        try {
            val previewTextView = keyboardRootView?.findViewById<TextView>(R.id.tv_keyboard_live_preview) ?: return
            if (composingText.isEmpty()) {
                previewTextView.visibility = View.GONE
            } else {
                previewTextView.visibility = View.VISIBLE

                val textToFormat = composingText.toString()
                var processed = UnicodeStylingEngine.applyStyle(textToFormat, activeUnicode)

                if (glitterEnabled) {
                    val glitterSymbols = listOf("✨", "🌟", "⭐", "💫")
                    val words = processed.split(" ")
                    val sb = StringBuilder()
                    for (i in words.indices) {
                        sb.append(words[i])
                        if (i < words.size - 1) {
                            val symbol = glitterSymbols[i % glitterSymbols.size]
                            sb.append(" $symbol ")
                        }
                    }
                    processed = if (words.size == 1) "✨ $processed ✨" else sb.toString()
                }

                processed = ShapeEngine.applyShape(processed, activeShape)
                processed = ArtEngine.applyFrame(processed, activeStyle)
                if (customSignature.isNotEmpty()) {
                    processed = "$processed\n$customSignature"
                }

                // Apply Rainbow Coloring and Giant Sizing preview style dynamically
                previewTextView.text = PreviewStyler.stylePreview(
                    processed,
                    colorfulPreviewEnabled,
                    giantWordsEnabled
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateSuggestionsBar() {
        try {
            val root = keyboardRootView ?: return
            val suggestionsBar = root.findViewById<View>(R.id.auto_suggestions_bar) ?: return
            if (!autoSuggestionsEnabled || composingText.isEmpty()) {
                suggestionsBar.visibility = if (autoSuggestionsEnabled) View.VISIBLE else View.GONE
                root.findViewById<TextView>(R.id.suggestion_left)?.text = "spell"
                root.findViewById<TextView>(R.id.suggestion_center)?.text = "SpellType"
                root.findViewById<TextView>(R.id.suggestion_right)?.text = "keyboard"
                suggestedStyleLeft = null
                suggestedStyleRight = null
                return
            }

            // Rule-based AI Mood detection in real-time as user types!
            val rawInput = composingText.toString()
            val moodSuggestion = MoodDetector.detectMood(rawInput)

            val leftText = root.findViewById<TextView>(R.id.suggestion_left) ?: return
            val centerText = root.findViewById<TextView>(R.id.suggestion_center) ?: return
            val rightText = root.findViewById<TextView>(R.id.suggestion_right) ?: return

            centerText.text = "Mood: ${moodSuggestion.mood.displayName} ${moodSuggestion.mood.emoji}"

            val list = moodSuggestion.suggestedStyles
            if (list.isNotEmpty()) {
                suggestedStyleLeft = list[0]
                leftText.text = "Try " + list[0].name.lowercase().replace("_", " ")
            } else {
                suggestedStyleLeft = null
                leftText.text = "Normal"
            }

            if (list.size > 1) {
                suggestedStyleRight = list[1]
                rightText.text = "Try " + list[1].name.lowercase().replace("_", " ")
            } else {
                suggestedStyleRight = null
                rightText.text = "Star"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyKeyboardHeight(heightSelection: String) {
        try {
            val container = keyboardRootView as? LinearLayout ?: return
            val density = resources.displayMetrics.density

            // Loop and adjust vertical row parameters
            for (i in 0 until container.childCount) {
                val row = container.getChildAt(i) as? LinearLayout ?: continue
                val rowId = row.id
                if (rowId == R.id.number_row || rowId == R.id.auto_suggestions_bar || row.childCount > 0) {
                    if (rowId == R.id.auto_suggestions_bar) continue

                    val lp = row.layoutParams as? LinearLayout.LayoutParams ?: continue
                    lp.height = when (heightSelection) {
                        "SMALL" -> (44 * density).toInt()
                        "LARGE" -> (64 * density).toInt()
                        else -> (54 * density).toInt() // MEDIUM
                    }
                    row.layoutParams = lp
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyCustomConfigurations() {
        try {
            val root = keyboardRootView ?: return

            // Base Theme Colors
            val baseBgColor = when (themeSelection) {
                "AMOLED" -> android.graphics.Color.BLACK
                "LIGHT" -> android.graphics.Color.parseColor("#F3F4F6")
                "BLUE" -> android.graphics.Color.parseColor("#1E3A8A")
                "PURPLE" -> android.graphics.Color.parseColor("#4C1D95")
                "GREEN" -> android.graphics.Color.parseColor("#064E3B")
                else -> android.graphics.Color.parseColor("#0B0F19") // DARK
            }

            val wallBgColor = when (keyboardWallpaperPath) {
                "OCEAN" -> android.graphics.Color.parseColor("#0F172A") // Deep ocean slate
                "SUNSET" -> android.graphics.Color.parseColor("#31102F") // Sunset glow
                "MIDNIGHT" -> android.graphics.Color.parseColor("#020617") // Midnight magic
                "GLASS" -> android.graphics.Color.parseColor("#121214") // Glassmorphic translucent dark
                else -> baseBgColor
            }

            // Apply Wallpaper/Theme with Opacity / Glassmorphic background
            if (keyboardWallpaperPath.isNotEmpty()) {
                val drawable = android.graphics.drawable.GradientDrawable()
                drawable.shape = android.graphics.drawable.GradientDrawable.RECTANGLE

                val alpha = (keyboardWallpaperOpacity * 2.55).toInt().coerceIn(0, 255)
                val blendedColor = android.graphics.Color.argb(
                    alpha,
                    android.graphics.Color.red(wallBgColor),
                    android.graphics.Color.green(wallBgColor),
                    android.graphics.Color.blue(wallBgColor)
                )
                drawable.setColor(blendedColor)
                root.background = drawable
            } else {
                root.setBackgroundColor(baseBgColor)
            }

            // Load theme_rewarded ad dynamically for premium theme selections
            if (!premiumUnlocked && (themeSelection == "BLUE" || themeSelection == "PURPLE" || themeSelection == "GREEN" || keyboardWallpaperPath.isNotEmpty())) {
                com.spelltype.keyboard.presentation.ads.AdManager.loadRewarded(
                    context = this,
                    onLoaded = { _ ->
                        // Preloaded successfully
                    }
                )
            }

            styleAllTextViewsUnder(root)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun styleAllTextViewsUnder(view: View) {
        try {
            if (view is TextView) {
                applyKeyStyle(view, false)
            } else if (view is android.view.ViewGroup) {
                for (i in 0 until view.childCount) {
                    styleAllTextViewsUnder(view.getChildAt(i))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyKeyStyle(view: TextView, isPressed: Boolean) {
        try {
            val id = view.id
            val isStandardKey = letterKeyIds.contains(id) || numKeyIds.contains(id)
            val isSpecialKey = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_space || id == R.id.btn_enter

            if (!isStandardKey && !isSpecialKey) return

            val density = resources.displayMetrics.density

            // A. Text Size
            val size = when (keyTextSize) {
                "SMALL" -> if (isStandardKey) 15f else 11f
                "LARGE" -> if (isStandardKey) 24f else 16f
                "HUGE" -> if (isStandardKey) 28f else 18f
                else -> if (isStandardKey) 19f else 13f // MEDIUM
            }
            view.textSize = size

            // B. Colors
            val isLight = themeSelection == "LIGHT"
            val baseKeyColor = when (themeSelection) {
                "AMOLED" -> android.graphics.Color.parseColor("#111111")
                "LIGHT" -> android.graphics.Color.parseColor("#FFFFFF")
                "BLUE" -> android.graphics.Color.parseColor("#3B82F6")
                "PURPLE" -> android.graphics.Color.parseColor("#7C3AED")
                "GREEN" -> android.graphics.Color.parseColor("#10B981")
                else -> android.graphics.Color.parseColor("#1F2937") // DARK
            }
            val baseSpecialColor = if (isLight) android.graphics.Color.parseColor("#E5E7EB") else android.graphics.Color.parseColor("#111827")
            var keyColor = if (isSpecialKey) baseSpecialColor else baseKeyColor

            // Handle Glow / pressed lighting state
            if (isPressed || glowingKeys.contains(id)) {
                keyColor = when (themeSelection) {
                    "LIGHT" -> android.graphics.Color.parseColor("#F43F5E") // Radiant Pink
                    "BLUE" -> android.graphics.Color.parseColor("#6EE7B7") // Neon Mint
                    "GREEN" -> android.graphics.Color.parseColor("#FBBF24") // Neon Amber
                    else -> android.graphics.Color.parseColor("#38BDF8") // Solar Cyan
                }
            }

            // Radius
            val radius = when (keyShape) {
                "SQUARE" -> 0f
                "CIRCULAR" -> 1000f
                "GLASSMORPHISM" -> 16f * density
                else -> 8f * density // ROUNDED
            }

            // Create LayerDrawable for 3D Bevel/Shadow Effect
            val shadowDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = radius
                setColor(android.graphics.Color.parseColor("#090D16")) // solid deep 3D bevel shadow
            }

            val frontDrawable = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = radius

                if (keyShape == "GLASSMORPHISM") {
                    val glassColor = if (isLight) {
                        android.graphics.Color.argb(if (isPressed || glowingKeys.contains(id)) 160 else 60, 255, 255, 255)
                    } else {
                        android.graphics.Color.argb(if (isPressed || glowingKeys.contains(id)) 140 else 45, 255, 255, 255)
                    }
                    setColor(glassColor)
                } else {
                    setColor(keyColor)
                }

                // Borders
                if (keyBorderEnabled) {
                    val thicknessPx = (keyBorderThickness * density).toInt().coerceAtLeast(1)
                    val borderColor = if (isPressed || glowingKeys.contains(id)) {
                        android.graphics.Color.WHITE
                    } else if (keyShape == "GLASSMORPHISM") {
                        if (isLight) android.graphics.Color.argb(120, 0, 0, 0)
                        else android.graphics.Color.argb(100, 255, 255, 255)
                    } else {
                        if (isLight) android.graphics.Color.parseColor("#D1D5DB")
                        else android.graphics.Color.parseColor("#4B5563")
                    }
                    setStroke(thicknessPx, borderColor)
                }
            }

            // Wrap in LayerDrawable to offset the front layer, creating an organic 3D push-button effect!
            val layers = arrayOf(shadowDrawable, frontDrawable)
            val layerDrawable = android.graphics.drawable.LayerDrawable(layers)

            // Offset front drawable to show the bottom shadow
            val shadowHeight = if (isPressed || glowingKeys.contains(id)) (1 * density).toInt() else (4 * density).toInt()
            layerDrawable.setLayerInset(1, 0, 0, 0, shadowHeight) // offset bottom

            view.background = layerDrawable

            // Text Color
            val textColor = if (isLight) {
                if (isPressed || glowingKeys.contains(id)) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#1F2937")
            } else {
                android.graphics.Color.WHITE
            }
            view.setTextColor(textColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
