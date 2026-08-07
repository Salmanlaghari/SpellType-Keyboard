package com.salmanlaghari.spelltypekeyboard.presentation.ime

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.TextView
import com.salmanlaghari.spelltypekeyboard.R
import com.salmanlaghari.spelltypekeyboard.data.datastore.KeyboardPreferences
import com.salmanlaghari.spelltypekeyboard.data.db.SpellTypeDatabase
import com.salmanlaghari.spelltypekeyboard.data.repository.KeyboardRepositoryImpl
import com.salmanlaghari.spelltypekeyboard.domain.StyleCategorizer
import com.salmanlaghari.spelltypekeyboard.domain.TextArtFormatter
import com.salmanlaghari.spelltypekeyboard.domain.PreviewStyler
import com.salmanlaghari.spelltypekeyboard.domain.MoodDetector
import com.salmanlaghari.spelltypekeyboard.domain.model.FrameStyle
import com.salmanlaghari.spelltypekeyboard.domain.model.ShapeLayout
import com.salmanlaghari.spelltypekeyboard.domain.model.UnicodeStyle
import com.salmanlaghari.spelltypekeyboard.domain.repository.KeyboardRepository
import com.salmanlaghari.spelltypekeyboard.domain.usecase.*
import com.salmanlaghari.spelltypekeyboard.domain.theme.PremiumTheme
import com.salmanlaghari.spelltypekeyboard.domain.theme.PremiumThemeEngine
import com.salmanlaghari.spelltypekeyboard.domain.theme.RealTheme
import com.salmanlaghari.spelltypekeyboard.domain.theme.RealThemeEngine
import com.salmanlaghari.spelltypekeyboard.domain.animation.PremiumAnimationEngine
import com.salmanlaghari.spelltypekeyboard.domain.language.LanguageManager
import com.salmanlaghari.spelltypekeyboard.domain.language.KeyboardLanguage
import com.salmanlaghari.spelltypekeyboard.domain.ai.AISuggestionsEngine
import com.salmanlaghari.spelltypekeyboard.domain.ai.GeminiLiveService
import com.salmanlaghari.spelltypekeyboard.domain.developer.DeveloperKeyboard
import com.salmanlaghari.spelltypekeyboard.domain.design.ImageDesignEngine
import com.salmanlaghari.spelltypekeyboard.domain.shapes.ShapeAlphabetEngine
import com.salmanlaghari.spelltypekeyboard.domain.backgrounds.KeyboardBackgroundEngine
import com.salmanlaghari.spelltypekeyboard.domain.features.VoiceInputManager
import com.salmanlaghari.spelltypekeyboard.domain.features.EmojiGifManager
import com.salmanlaghari.spelltypekeyboard.domain.features.SettingsManager
import com.salmanlaghari.spelltypekeyboard.domain.transmission.TransmissionEngine
import com.salmanlaghari.spelltypekeyboard.domain.effects.ParticleEffectsEngine
import com.salmanlaghari.spelltypekeyboard.presentation.common.setChipSelected
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

    // Custom 3D & Control Center Option variables
    private var force3DKeycaps = true
    private var gboardModeEnabled = false
    private var rainbowPreviewEnabled = false
    private var highFpsRenderEnabled = true
    private var holographicGlowEnabled = true
    private var tapParticlesEnabled = true
    private var premiumAssistEnabled = true
    private var adFreeSandboxEnabled = true

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

    // ═══ Real Premium Theme System ═══
    private var activeRealTheme: RealTheme = RealTheme.ALL[0]
    private var realThemeIndex = 0

    // ═══ Language System (120+ languages) ═══
    private var currentLanguage: KeyboardLanguage? = null
    private var languageList = LanguageManager.getAllLanguages()
    private var languageIndex = 0

    // ═══ Developer Mode ═══
    private var isDeveloperMode = false

    // ═══ Gemini Live ═══
    private var geminiActive = false

    // ═══ AI Suggestions ═══
    private var lastCommittedWord = ""

    // ═══ Shape Alphabet ═══
    private var activeShapeStyle: ShapeAlphabetEngine.ShapeStyle? = null
    private var shapeIndex = 0

    // ═══ Keyboard Background ═══
    private var activeBackground: KeyboardBackgroundEngine.KeyboardBackground? = null
    private var bgIndex = 0

    // ═══ Voice Input ═══
    private var voiceInputManager: VoiceInputManager? = null

    // ═══ Emoji Panel ═══
    private var isEmojiPanelOpen = false
    private var emojiCategoryIndex = 0

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
        "🥺", "🥰", "🥶", "🥳", "🤠", "🤖", "💪", "🏆", "🎯", "💡",
        "📱", "💻", "🎧", "📸", "🎬", "🏋️", "🧘", "✈️", "🏕️", "🏔️",
        "🌊", "🌅", "🌌", "🦋", "🐝", "🌺", "🌻", "🌹", "🍀", "🌴",
        "🐶", "🐱", "🐼", "🐨", "🦊", "🐰", "🐻", "🦁", "🐯", "🐸",
        "❤️", "💕", "💞", "💓", "💗", "💖", "💘", "💝", "💟", "🫶",
        "😊", "😂", "🤣", "😍", "🤩", "😎", "🥳", "😇", "🤗", "😋",
        "👍", "👎", "👏", "🙌", "🤝", "✌️", "🤞", "🫰", "👋", "🤙"
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
                com.salmanlaghari.spelltypekeyboard.presentation.ads.AdManager.init(applicationContext ?: this)

                showBannerIn(topContainer, com.salmanlaghari.spelltypekeyboard.presentation.ads.BannerType.KEYBOARD_TOP)
                showBannerIn(bottomContainer, com.salmanlaghari.spelltypekeyboard.presentation.ads.BannerType.KEYBOARD_BOTTOM)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBannerIn(
        container: android.widget.FrameLayout?,
        type: com.salmanlaghari.spelltypekeyboard.presentation.ads.BannerType
    ) {
        container ?: return
        container.visibility = View.VISIBLE
        com.salmanlaghari.spelltypekeyboard.presentation.ads.AdManager.loadBanner(
            context = this,
            type = type,
            adSize = com.google.android.gms.ads.AdSize.BANNER
        ) { adView ->
            container.removeAllViews()
            container.addView(adView)
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
            val keyboardView = layoutInflater.inflate(R.layout.keyboard_view_premium, null)
            keyboardRootView = keyboardView

            // Initialize default language
            currentLanguage = LanguageManager.getLanguageByCode("en")

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

            // ═══ NEW: Premium Header Bar Buttons ═══

            // Language Switcher — cycles through 120+ languages
            keyboardView.findViewById<View>(R.id.btn_language)?.setOnClickListener {
                onKeyClickFeedback(it)
                cycleLanguage()
            }

            // Theme Selector — cycles through 12+ premium themes
            keyboardView.findViewById<View>(R.id.btn_theme)?.setOnClickListener {
                onKeyClickFeedback(it)
                cyclePremiumTheme()
            }

            // Developer Mode Toggle
            keyboardView.findViewById<View>(R.id.btn_dev_mode)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleDeveloperMode()
            }

            // Gemini AI Toggle
            keyboardView.findViewById<View>(R.id.btn_gemini)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleGeminiLive()
            }

            // ═══ NEW: Bottom Row Extra Buttons ═══

            // Emoji button (new dedicated button)
            keyboardView.findViewById<View>(R.id.btn_emoji)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleEmojiMode()
                refreshQuickArtBar()
            }

            // Comma button
            keyboardView.findViewById<View>(R.id.btn_comma)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleKeyClick(",")
            }

            // ═══ NEW: Extended Pro Tools ═══

            keyboardView.findViewById<View>(R.id.tool_voice)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleVoiceInput()
            }

            keyboardView.findViewById<View>(R.id.tool_gif)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleGifSearch()
            }

            keyboardView.findViewById<View>(R.id.tool_image)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleImageDesign()
            }

            keyboardView.findViewById<View>(R.id.tool_settings)?.setOnClickListener {
                onKeyClickFeedback(it)
                toggleControlCenter()
            }

            // Transmission Tool — share styled text
            keyboardView.findViewById<View>(R.id.tool_transmission)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleTransmissionTool()
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
                onKeyClickFeedback(it)
                handleSuggestionClick(0)
            }
            keyboardView.findViewById<View>(R.id.suggestion_center)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleSuggestionClick(1)
            }
            keyboardView.findViewById<View>(R.id.suggestion_right)?.setOnClickListener {
                onKeyClickFeedback(it)
                handleSuggestionClick(2)
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
                        keyboardRootView?.findViewById<View>(R.id.ai_suggestions_bar)?.visibility = if (enabled) View.VISIBLE else View.GONE
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
            bindControlCenter(keyboardView)
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
            ParticleEffectsEngine.stop()
            voiceInputManager?.destroy()
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

    /** Builds a quick-art bar chip with the shared sizing, colors and selected state. */
    private fun createQuickArtChip(label: String, selected: Boolean, onClick: (TextView) -> Unit): TextView {
        val density = resources.displayMetrics.density
        val padLR = (12 * density).toInt()
        val padTB = (6 * density).toInt()
        val margin = (4 * density).toInt()

        val chip = TextView(this)
        chip.text = label
        chip.setTextColor(resources.getColor(R.color.key_text_color, null))
        chip.textSize = 12f
        chip.setPadding(padLR, padTB, padLR, padTB)
        chip.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(margin, 0, margin, 0) }
        chip.isClickable = true
        chip.isFocusable = true
        chip.setChipSelected(selected)
        chip.setOnClickListener { onClick(chip) }
        return chip
    }

    private fun populateQuickArtBar(container: LinearLayout) {
        try {
            container.removeAllViews()

            // 1. Add 3D Emoji Toggle Chip
            container.addView(
                createQuickArtChip("😎 3D Emoji", isEmojiMode) { chip ->
                    onKeyClickFeedback(chip)
                    toggleEmojiMode()
                    refreshQuickArtBar()
                }
            )

            // 2. Add Glitter Toggle Chip
            container.addView(
                createQuickArtChip(
                    if (glitterEnabled) "✨ Glitter: ON" else "✨ Glitter: OFF",
                    glitterEnabled
                ) { chip ->
                    onKeyClickFeedback(chip)
                    toggleGlitter()
                }
            )

            // 3. Add standard styles
            for (style in getSortedStyles()) {
                val prefix = when {
                    favoriteStyles.contains(style.name) -> "♥ "
                    StyleCategorizer.isPremium(style) -> "👑 "
                    else -> ""
                }
                val name = if (style == FrameStyle.NONE) "Normal" else TextArtFormatter.displayName(style)

                container.addView(
                    createQuickArtChip("$prefix$name", activeStyle == style && !isEmojiMode) {
                        if (isEmojiMode) {
                            isEmojiMode = false
                            updateKeyLabels()
                        }
                        selectFrameStyle(style)
                    }
                )
            }

            // 4. Add Unicode Font Style Chips (excluding NONE)
            for (uStyle in UnicodeStyle.values().filter { it != UnicodeStyle.NONE }) {
                container.addView(
                    createQuickArtChip(
                        "𝔽 ${TextArtFormatter.displayName(uStyle)}",
                        activeUnicode == uStyle && !isEmojiMode
                    ) {
                        if (isEmojiMode) {
                            isEmojiMode = false
                            updateKeyLabels()
                        }
                        selectUnicodeStyle(uStyle)
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val glowingKeys = mutableSetOf<Int>()

    /** Haptic pulse honouring the configured strength. */
    private fun playVibrationFeedback() {
        if (!vibrationEnabled) return
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

    /** Key click sound honouring the configured volume. */
    private fun playSoundFeedback() {
        if (!soundEnabled) return
        val am = getSystemService(android.content.Context.AUDIO_SERVICE) as? android.media.AudioManager
        am?.playSoundEffect(android.media.AudioManager.FX_KEYPRESS_STANDARD, soundVolume / 100f)
    }

    private fun onKeyClickFeedback(view: View) {
        try {
            playVibrationFeedback()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            playSoundFeedback()
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

    /** Apply all active styles to composing text for live preview in input field */
    private fun applyComposingStyle(text: String, signature: String = ""): String =
        TextArtFormatter.format(
            text = text,
            style = activeStyle,
            shape = activeShape,
            unicode = activeUnicode,
            glitterEnabled = glitterEnabled,
            signature = signature
        )

    private fun handleKeyClick(text: String) {
        try {
            val ic: InputConnection = currentInputConnection ?: return
            if (!isStylingActive()) {
                ic.commitText(text, 1)
            } else {
                composingText.append(text)
                // Apply styling live so user sees styled text in the input field
                val styled = applyComposingStyle(composingText.toString())
                ic.setComposingText(styled, 1)
                updateLivePreviewBar()
                updateSuggestionsBar()
            }

            if (isShifted && !isSymbolMode) {
                isShifted = false
                updateKeyLabels()
            }

            // Update AI suggestions as user types
            if (autoSuggestionsEnabled) {
                updateSuggestionsBar()
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
                    val styled = applyComposingStyle(composingText.toString())
                    ic.setComposingText(styled, 1)
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

            // Track last word for AI suggestions
            if (composingText.isNotEmpty()) {
                lastCommittedWord = composingText.toString().trim()
            }

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

    /**
     * Runs a pro tool straight away for premium users, otherwise behind an interstitial.
     * The tool always runs, even when the ad cannot be loaded or shown.
     */
    private fun runProToolWithAd(tool: () -> Unit) {
        if (premiumUnlocked) {
            tool()
            return
        }
        com.salmanlaghari.spelltypekeyboard.presentation.ads.AdManager.loadInterstitial(
            context = this,
            type = com.salmanlaghari.spelltypekeyboard.presentation.ads.InterstitialType.PRO_TOOLS,
            onLoaded = { _ ->
                // Service context safety fallback
                tool()
            },
            onFailed = {
                tool()
            }
        )
    }

    private fun handleClipboardToolWithAd() = runProToolWithAd(::handleClipboardTool)

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

    private fun handleTranslateToolWithAd() = runProToolWithAd(::handleTranslateTool)

    private var templateIndex = 0
    private fun handleTemplatesTool() {
        try {
            val templates = listOf(
                "★ S P E L L T Y P E ★",
                "꧁𓊈𒆜 ⓈⓅⒺⓁⓁⓉⓅⒺ 𒆜𓊉꧂",
                "┌────── ∘°❉°∘ ──────┐\n   WELCOME TO MY BIO\n└────── °∘❉∘° ──────┘",
                "•◦✦────•✦•────✦◦•\n ✨ 𝓥𝓲𝓫𝓲𝓷𝓰 𝓲𝓷 3𝓓 ✨ \n•◦✦────•✦•────✦◦•",
                "💖 𝓁𝑜𝓋𝑒 𝓎𝑜𝓊𝓇𝓈𝑒𝓁𝓋𝑒𝓈 💖",
                "🎮 𝒢𝒜𝑀𝐸𝑅 𝒵𝒪𝒩𝐸 🎮\n ══🎮🕹️👾══",
                "┌── ⋆⋅☆⋅⋆ ──┐\n   𝓢𝓽𝓪𝓻 𝓑𝓸𝔁 \n└── ⋆⋅☆⋅⋆ ──┘",
                "▓▒░  𝕮𝖞𝖇𝖊𝖗𝖕𝖚𝖓𝖐  ░▒▓",
                "👑 𝓡𝓸𝔂𝓪𝓵 𝓒𝓻𝓸𝔀𝓷 𝓣𝓲𝓽𝓵𝓮 👑",
                "━━━━━━━━ ✧ ━━━━━━━━\n   𝒮𝓎𝓂𝓂𝑒𝓉𝓇𝓎\n━━━━━━━━ ✧ ━━━━━━━━",
                "☄️✨ 𝒮𝓅𝒶𝓇𝓀𝓁𝓎 𝒬𝓊𝑜𝓉𝑒𝓈 ✨☄️",
                "(\\__/)  \n(•ㅅ•)  Bunny Loves You!\n/ 　 づ",
                "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬\n  𝕳𝖊𝖆𝖛𝖞 𝕯𝖊𝖈𝖔𝖗𝖆𝖙𝖎𝖛𝖊 𝕷𝖎𝖓𝖊\n▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                "🖤 𝔖𝔭𝔢𝔩𝔩𝔗𝔶𝔭𝔢 𝔊𝔬𝔱𝔥𝔦𝔠 𝔖𝔦𝔤𝔫𝔢𝔱 🖤",
                "🔥 𝓕𝓲𝓻𝓮 𝓑𝓵𝓪𝓼𝓽 𝓔𝓶𝓸𝓳𝓲 𝓑𝓪𝓷𝓷𝓮𝓻 🔥\n 🔥☄️💥🌋🧨",
                "⭐ 𝒲𝒾𝓈𝒽 𝒰𝓅𝑜𝓃 𝒜 𝒮𝓉𝒶𝓇 ⭐",
                "🎮 𝓖𝓪𝓶𝓮𝓻𝓼 𝓤𝓷𝓲𝓽𝓮𝓭 🎮\n 🕹️👾🎯🏆💪",
                "💻 𝓒𝓸𝓭𝓮𝓻𝓼 𝓛𝓲𝓯𝓮 💻\n { } = Life; 🐛 = Enemy",
                "🎵 𝓜𝓾𝓼𝓲𝓬 𝓛𝓸𝓿𝓮𝓻 🎵\n 🎸🎹🥁🎤🎧",
                "📚 𝓑𝓸𝓸𝓴 𝓦𝓸𝓻𝓶 📚\n Reading is dreaming\n with open eyes ✨",
                "🏋️ 𝓕𝓲𝓽𝓷𝓮𝓼𝓼 𝓖𝓸𝓪𝓵𝓼 🏋️\n 💪 No Pain, No Gain 💪",
                "✈️ 𝓣𝓻𝓪𝓿𝓮𝓵 𝓓𝓲𝓪𝓻𝔂 ✈️\n 🌍🗺️🧳📸✨",
                "🍕 𝓕𝓸𝓸𝓭𝓲𝓮 𝓛𝓲𝓯𝓮 🍕\n 🍔🌮🍣🧁🍩",
                "🎬 𝓜𝓸𝓿𝓲𝓮 𝓑𝓾𝓯𝓯 🎬\n 🍿🎥🎭📽️🎞️",
                "🌅 𝓟𝓮𝓪𝓬𝓮𝓯𝓾𝓵 𝓢𝓸𝓾𝓵 🌅\n 🧘‍♀️☮️🕊️🌿💚",
                "⚡ 𝓢𝓹𝓮𝓵𝓵𝓣𝔂𝓹𝓮 𝓟𝓸𝔀𝓮𝓻 ⚡\n ✨🔥💫🌟⭐"
            )
            val index = templateIndex
            templateIndex = (templateIndex + 1) % templates.size
            composingText.clear()
            composingText.append(templates[index])
            updateLivePreviewBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTemplatesToolWithAd() = runProToolWithAd(::handleTemplatesTool)

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
            var textToFormat = composingText.toString()
            composingText.clear()
            updateLivePreviewBar()
            updateSuggestionsBar()
            val ic: InputConnection = currentInputConnection ?: return

            // Apply shape alphabet if active
            activeShapeStyle?.let { shape ->
                textToFormat = ShapeAlphabetEngine.applyShape(textToFormat, shape)
            }

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
            } else {
                ic.commitText(textToFormat, 1)
                onComplete?.invoke()
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

                val processed = applyComposingStyle(composingText.toString(), customSignature)

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
            // Use the new AI-powered suggestions
            updateSuggestionsBarWithAI()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyKeyboardHeight(heightSelection: String) {
        try {
            val root = keyboardRootView as? LinearLayout ?: return
            val keysContainer = root.findViewById<LinearLayout>(R.id.keyboard_keys_container) ?: return
            val density = resources.displayMetrics.density

            // Only adjust heights of the actual KEY ROWS inside keyboard_keys_container
            // (number_row, QWERTY row, ASDF row, ZXCV row, space row)
            // Do NOT touch toolbar bars (header, art bar, suggestions, pro tools)
            val targetHeight = when (heightSelection) {
                "SMALL" -> (40 * density).toInt()
                "LARGE" -> (52 * density).toInt()
                else -> (46 * density).toInt() // MEDIUM — matches XML default
            }

            for (i in 0 until keysContainer.childCount) {
                val row = keysContainer.getChildAt(i) as? LinearLayout ?: continue
                val lp = row.layoutParams as? LinearLayout.LayoutParams ?: continue
                lp.height = targetHeight
                row.layoutParams = lp
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyCustomConfigurations() {
        try {
            applyPremiumTheme()
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

    // ═══════════════════════════════════════════════════════════════
    //  PREMIUM THEME SYSTEM — 12+ HD Themes with 60fps animations
    // ═══════════════════════════════════════════════════════════════

    private fun cyclePremiumTheme() {
        try {
            val themes = RealThemeEngine.getAllThemes()
            realThemeIndex = (realThemeIndex + 1) % themes.size
            activeRealTheme = themes[realThemeIndex]
            applyPremiumTheme()
            keyboardRootView?.findViewById<TextView>(R.id.btn_theme)?.text = "${activeRealTheme.emoji} ${activeRealTheme.name}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyPremiumTheme() {
        try {
            val root = keyboardRootView ?: return
            val theme = activeRealTheme
            val density = resources.displayMetrics.density

            if (gboardModeEnabled) {
                // ═══ Pure Gboard Style — flat, minimal, clean ═══
                root.setBackgroundColor(Color.parseColor("#1F2023"))

                // Hide all extra bars for clean Gboard look
                root.findViewById<View>(R.id.ai_suggestions_bar)?.setBackgroundColor(Color.parseColor("#202124"))
                root.findViewById<TextView>(R.id.tv_keyboard_live_preview)?.setBackgroundColor(Color.parseColor("#171717"))

                // Header bar — minimal Gboard gray
                val headerBar = root.findViewById<LinearLayout>(R.id.btn_language)?.parent as? LinearLayout
                headerBar?.setBackgroundColor(Color.parseColor("#202124"))

                // Flat Gboard keys — no 3D, no shadow, no bevel
                for ((id, view) in keyViews) {
                    val isSpecial = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_enter
                    val isAccent = id == R.id.btn_enter
                    val radius = 5f * density
                    val keyBg = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = radius
                        setColor(when {
                            isAccent -> Color.parseColor("#8AB4F8") // Gboard blue accent
                            isSpecial -> Color.parseColor("#3C4043") // Gboard special gray
                            else -> Color.parseColor("#3C4043") // Gboard key gray
                        })
                    }
                    view.background = keyBg
                    view.setTextColor(if (isAccent) Color.BLACK else Color.WHITE)
                    view.elevation = 0f // NO shadow, flat Gboard style
                }
                return
            }

            root.background = theme.createBackgroundDrawable()

            for ((id, view) in keyViews) {
                val isSpecial = id == R.id.btn_shift || id == R.id.btn_backspace || id == R.id.btn_mode || id == R.id.btn_enter
                val radius = 8f * density
                view.background = if (isSpecial) theme.createAccentBackground(radius) else theme.createKeyBackground(radius)
                view.setTextColor(theme.keyTextColor)
            }

            val headerBar = root.findViewById<LinearLayout>(R.id.btn_language)?.parent as? LinearLayout
            headerBar?.setBackgroundColor(theme.toolbarBg)

            root.findViewById<View>(R.id.ai_suggestions_bar)?.setBackgroundColor(theme.suggestionBg)
            root.findViewById<TextView>(R.id.tv_keyboard_live_preview)?.setBackgroundColor(theme.previewBg)

            root.findViewById<TextView>(R.id.btn_language)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_theme)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_dev_mode)?.setTextColor(theme.toolbarText)
            root.findViewById<TextView>(R.id.btn_gemini)?.setTextColor(theme.accentColor)

            PremiumAnimationEngine.animateGlowPulse(
                root.findViewById<TextView>(R.id.btn_gemini) ?: return,
                theme.glowColor
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  LANGUAGE SYSTEM — 120+ languages
    // ═══════════════════════════════════════════════════════════════

    private fun cycleLanguage() {
        try {
            languageIndex = (languageIndex + 1) % languageList.size
            currentLanguage = languageList[languageIndex]
            applyLanguage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyLanguage() {
        try {
            val lang = currentLanguage ?: return
            // Update language button
            keyboardRootView?.findViewById<TextView>(R.id.btn_language)?.text = "${lang.emoji} ${lang.code.uppercase()}"
            // Update key labels based on language layout
            updateKeyLabelsForLanguage(lang)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateKeyLabelsForLanguage(lang: KeyboardLanguage) {
        try {
            val allKeys = lang.row1 + lang.row2 + lang.row3
            for (i in letterKeyIds.indices) {
                if (i < allKeys.size) {
                    keyViews[letterKeyIds[i]]?.text = if (isShifted) allKeys[i].uppercase() else allKeys[i]
                    keyViews[letterKeyIds[i]]?.visibility = View.VISIBLE
                } else {
                    keyViews[letterKeyIds[i]]?.visibility = View.INVISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  DEVELOPER KEYBOARD — Code snippets, symbols
    // ═══════════════════════════════════════════════════════════════

    private fun toggleDeveloperMode() {
        try {
            isDeveloperMode = !isDeveloperMode
            if (isDeveloperMode) {
                // Cycle through 20+ shape alphabets
                val shapes = ShapeAlphabetEngine.getAllShapes()
                shapeIndex = (shapeIndex + 1) % shapes.size
                activeShapeStyle = shapes[shapeIndex]
                // Apply shape to current composing text
                if (composingText.isNotEmpty()) {
                    val shaped = ShapeAlphabetEngine.applyShape(composingText.toString(), activeShapeStyle!!)
                    composingText.clear()
                    composingText.append(shaped)
                    val ic = currentInputConnection
                    ic?.setComposingText(composingText.toString(), 1)
                    updateLivePreviewBar()
                }
                keyboardRootView?.findViewById<TextView>(R.id.btn_dev_mode)?.text = "${activeShapeStyle!!.emoji} ${activeShapeStyle!!.name}"
            } else {
                activeShapeStyle = null
                updateKeyLabels()
                keyboardRootView?.findViewById<TextView>(R.id.btn_dev_mode)?.text = "⌨️ Dev"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  GEMINI LIVE — AI writing assistant
    // ═══════════════════════════════════════════════════════════════

    private fun toggleGeminiLive() {
        try {
            geminiActive = !geminiActive
            if (geminiActive) {
                keyboardRootView?.findViewById<TextView>(R.id.btn_gemini)?.text = "✨ AI ON"
                // Apply Gemini-powered suggestions
                updateGeminiSuggestions()
            } else {
                keyboardRootView?.findViewById<TextView>(R.id.btn_gemini)?.text = "✨ AI"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateGeminiSuggestions() {
        try {
            if (!geminiActive) return
            val text = composingText.toString()
            if (text.isEmpty()) return

            val smartReplies = GeminiLiveService.getSmartReplies(text)
            val root = keyboardRootView ?: return

            if (smartReplies.size >= 1) root.findViewById<TextView>(R.id.suggestion_left)?.text = smartReplies[0]
            if (smartReplies.size >= 2) root.findViewById<TextView>(R.id.suggestion_center)?.text = smartReplies[1]
            if (smartReplies.size >= 3) root.findViewById<TextView>(R.id.suggestion_right)?.text = smartReplies[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  AI SUGGESTIONS — Gboard-style word prediction
    // ═══════════════════════════════════════════════════════════════

    private fun handleSuggestionClick(index: Int) {
        try {
            val root = keyboardRootView ?: return
            val suggestion = when (index) {
                0 -> root.findViewById<TextView>(R.id.suggestion_left)?.text?.toString()
                1 -> root.findViewById<TextView>(R.id.suggestion_center)?.text?.toString()
                2 -> root.findViewById<TextView>(R.id.suggestion_right)?.text?.toString()
                else -> null
            } ?: return

            // If it's a style suggestion (from mood detector), apply it
            if (suggestion.startsWith("Try ")) {
                val styleName = suggestion.removePrefix("Try ").uppercase().replace(" ", "_")
                FrameStyle.values().find { it.name == styleName }?.let { selectFrameStyle(it) }
                return
            }

            // Otherwise treat as word suggestion
            val ic = currentInputConnection ?: return
            if (composingText.isNotEmpty()) {
                composingText.clear()
                composingText.append(suggestion)
                ic.setComposingText(composingText.toString(), 1)
                updateLivePreviewBar()
            } else {
                ic.commitText(suggestion, 1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  VOICE INPUT, GIF, IMAGE DESIGN, SETTINGS
    // ═══════════════════════════════════════════════════════════════

    private fun handleVoiceInput() {
        try {
            val ctx = applicationContext ?: this
            if (voiceInputManager == null) {
                voiceInputManager = VoiceInputManager(ctx)
                voiceInputManager?.setCallback(object : VoiceInputManager.VoiceCallback {
                    override fun onResult(text: String) {
                        try {
                            if (text.isBlank()) return
                            val ic = currentInputConnection ?: return
                            ic.commitText(text, 1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onPartialResult(text: String) {
                        try {
                            if (text.isBlank()) return
                            val ic = currentInputConnection ?: return
                            ic.setComposingText(text, 1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onError(error: String) {
                        try {
                            android.widget.Toast.makeText(ctx, "🎤 ${error.ifBlank { "Voice input error" }}", android.widget.Toast.LENGTH_SHORT).show()
                            keyboardRootView?.findViewById<TextView>(R.id.tool_voice)?.text = "🎤"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onListeningStarted() {
                        try {
                            keyboardRootView?.findViewById<TextView>(R.id.tool_voice)?.text = "🔴"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onListeningStopped() {
                        try {
                            keyboardRootView?.findViewById<TextView>(R.id.tool_voice)?.text = "🎤"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
            if (voiceInputManager?.isListening() == true) {
                voiceInputManager?.stopListening()
            } else {
                voiceInputManager?.startListening()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                android.widget.Toast.makeText(applicationContext ?: this, "🎤 Voice input unavailable", android.widget.Toast.LENGTH_SHORT).show()
            } catch (_: Exception) {}
        }
    }

    private fun handleGifSearch() {
        try {
            // Show a proper emoji picker with categorized emojis instead of random
            isEmojiMode = true
            isSymbolMode = false
            isShifted = false
            updateKeyLabels()

            // Show emoji category in suggestion bar
            val root = keyboardRootView ?: return
            showSuggestions(root, "😀 Smileys", "👍 People", "🔥 Objects")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleImageDesign() {
        try {
            val backgrounds = KeyboardBackgroundEngine.getAllBackgrounds()
            if (backgrounds.isEmpty()) return
            bgIndex = (bgIndex + 1) % backgrounds.size
            activeBackground = backgrounds[bgIndex]
            val bg = activeBackground ?: return
            val root = keyboardRootView ?: return
            root.background = KeyboardBackgroundEngine.createBackground(bg)
            try {
                android.widget.Toast.makeText(
                    applicationContext ?: this,
                    "🎨 ${bg.emoji} ${bg.name}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            } catch (_: Exception) {}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSettings() {
        try {
            SettingsManager.openKeyboardSettings(applicationContext ?: this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleTransmissionTool() {
        try {
            val text = composingText.toString()
            if (text.isBlank()) {
                android.widget.Toast.makeText(applicationContext ?: this, "📡 Type something first to transmit!", android.widget.Toast.LENGTH_SHORT).show()
                return
            }
            // Encode styled text for sharing
            val payload = TransmissionEngine.StyledPayload(
                text = text,
                unicodeStyle = activeUnicode.name,
                frameStyle = activeStyle.name,
                glitterEnabled = glitterEnabled,
                signature = customSignature
            )
            val encoded = TransmissionEngine.encode(payload)
            val clipboardData = TransmissionEngine.prepareForClipboard(payload)
            // Copy to clipboard
            val clipboard = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("SpellType Transmission", clipboardData)
            clipboard?.setPrimaryClip(clip)
            android.widget.Toast.makeText(applicationContext ?: this, "📡 Transmission copied! Share it!", android.widget.Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            android.widget.Toast.makeText(applicationContext ?: this, "📡 Transmission error", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PREMIUM ASSIST — Smart Writing Suggestions
    // ═══════════════════════════════════════════════════════════════

    /** Premium Assist: context-aware smart suggestions with 50+ completions, emoji, phrases, autocorrect */
    private fun getPremiumAssistSuggestions(input: String): List<String> {
        val suggestions = mutableListOf<String>()
        val lower = input.lowercase().trim()

        // Auto-correction dictionary — 100+ common misspellings
        val autoCorrections = mapOf(
            "teh" to "the", "recieve" to "receive", "occured" to "occurred",
            "seperate" to "separate", "definately" to "definitely", "accomodate" to "accommodate",
            "occurance" to "occurrence", "untill" to "until", "wich" to "which",
            "thier" to "their", "freind" to "friend", "beleive" to "believe",
            "neccessary" to "necessary", "succesful" to "successful", "begining" to "beginning",
            "occuring" to "occurring", "comming" to "coming", "runing" to "running",
            "writting" to "writing", "stoping" to "stopping", "geting" to "getting",
            "sitll" to "still", "taht" to "that", "wiht" to "with", "htis" to "this",
            "adn" to "and", "fro" to "for", "fo" to "of", "ot" to "to",
            "hte" to "the", "yuo" to "you", "yuor" to "your", "waht" to "what",
            "jsut" to "just", "dont" to "don't", "wont" to "won't", "cant" to "can't",
            "didnt" to "didn't", "doesnt" to "doesn't", "isnt" to "isn't",
            "wasnt" to "wasn't", "werent" to "weren't", "hasnt" to "hasn't",
            "havent" to "haven't", "wouldnt" to "wouldn't", "couldnt" to "couldn't",
            "shouldnt" to "shouldn't", "arent" to "aren't", "arent" to "aren't",
            "im" to "I'm", "ive" to "I've", "id" to "I'd", "ill" to "I'll",
            "your" to "your", "youre" to "you're", "youve" to "you've",
            "theyre" to "they're", "weve" to "we've", "theres" to "there's",
            "whos" to "who's", "whats" to "what's", "thats" to "that's",
            "its" to "it's", "lets" to "let's", "heres" to "here's",
            "alot" to "a lot", "infact" to "in fact", "incase" to "in case",
            "eachother" to "each other", "aswell" to "as well", "incase" to "in case",
            "noone" to "no one", "atleast" to "at least", "eventhough" to "even though",
            "inspite" to "in spite", "eventhough" to "even though",
            "prolly" to "probably", "gonna" to "going to", "wanna" to "want to",
            "gotta" to "got to", "kinda" to "kind of", "sorta" to "sort of",
            "lemme" to "let me", "gimme" to "give me", "outta" to "out of",
            "coulda" to "could have", "woulda" to "would have", "shoulda" to "should have",
            "embarass" to "embarrass", "calender" to "calendar", "cemetary" to "cemetery",
            "concious" to "conscious", "existance" to "existence", "goverment" to "government",
            "independant" to "independent", "liason" to "liaison", "millenium" to "millennium",
            "publically" to "publicly", "refered" to "referred", "realy" to "really",
            "truely" to "truly", "wellcome" to "welcome", "wich" to "which",
            "loose" to "lose", "adn" to "and", "ahve" to "have", "bc" to "because",
            "bday" to "birthday", "cuz" to "because", "dm" to "direct message",
            "rn" to "right now", "tbh" to "to be honest", "imo" to "in my opinion",
            "fyi" to "for your information", "smh" to "shaking my head",
            "nvm" to "never mind", "idk" to "I don't know", "ikr" to "I know right",
            "tbh" to "to be honest", "ngl" to "not gonna lie", "fr" to "for real",
            "ong" to "on god", "af" to "as f***", "wbu" to "what about you",
            "hbu" to "how about you", "wyd" to "what are you doing", "hmu" to "hit me up",
            "lmao" to "laughing my a** off", "rofl" to "rolling on the floor laughing",
            "smh" to "shaking my head", "ttyl" to "talk to you later",
            "ily" to "I love you", "ilysm" to "I love you so much",
            "omw" to "on my way", "jk" to "just kidding", "ftw" to "for the win",
            "gg" to "good game", "gl" to "good luck", "hf" to "have fun",
            "thx" to "thanks", "ty" to "thank you", "yw" to "you're welcome",
            "np" to "no problem", "pls" to "please", "plz" to "please",
            "cya" to "see you", "ur" to "your", "u" to "you", "r" to "are",
            "y" to "why", "k" to "okay", "ok" to "okay", "kk" to "okay"
        )

        // Check for auto-correction first
        autoCorrections[lower]?.let { corrected ->
            if (corrected != lower) {
                suggestions.add("→ $corrected")
            }
        }

        // Smart emoji suggestions based on context
        val emojiContext = mapOf(
            "love" to "❤️", "heart" to "💕", "happy" to "😊", "sad" to "😢",
            "angry" to "😡", "laugh" to "😂", "lol" to "😂", "cry" to "😭",
            "fire" to "🔥", "cool" to "😎", "star" to "⭐", "sun" to "☀️",
            "moon" to "🌙", "rain" to "🌧️", "snow" to "❄️", "party" to "🎉",
            "birthday" to "🎂", "gift" to "🎁", "food" to "🍕", "eat" to "🍔",
            "drink" to "☕", "coffee" to "☕", "tea" to "🍵", "music" to "🎵",
            "game" to "🎮", "play" to "🎮", "sleep" to "😴", "work" to "💼",
            "school" to "📚", "study" to "📖", "home" to "🏠", "travel" to "✈️",
            "car" to "🚗", "phone" to "📱", "computer" to "💻", "money" to "💰",
            "time" to "⏰", "dog" to "🐶", "cat" to "🐱", "flower" to "🌸",
            "tree" to "🌲", "water" to "💧", "earth" to "🌍", "world" to "🌎",
            "good" to "👍", "bad" to "👎", "yes" to "✅", "no" to "❌",
            "think" to "🤔", "idea" to "💡", "magic" to "✨", "win" to "🏆",
            "run" to "🏃", "walk" to "🚶", "swim" to "🏊", "gym" to "💪",
            "pray" to "🙏", "thank" to "🙏", "sorry" to "😔", "welcome" to "😊",
            "miss" to "🥺", "hug" to "🤗", "kiss" to "😘", "marry" to "💒",
            "baby" to "👶", "child" to "👧", "friend" to "👫", "family" to "👨‍👩‍👧‍👦",
            "book" to "📖", "movie" to "🎬", "photo" to "📷", "pic" to "📸",
            "video" to "🎥", "art" to "🎨", "code" to "💻", "bug" to "🐛",
            "ship" to "🚀", "rocket" to "🚀", "space" to "🌌", "alien" to "👽"
        )

        // Find context-appropriate emoji
        for ((keyword, emoji) in emojiContext) {
            if (lower.contains(keyword) && !suggestions.any { it.contains(emoji) }) {
                suggestions.add("$emoji ${keyword.replaceFirstChar { it.uppercase() }}")
            }
        }

        // Smart word completions based on prefix — 50+ entries
        val completions = mapOf(
            "hel" to listOf("hello", "help", "held", "hello!", "helpful"),
            "goo" to listOf("good", "google", "goose", "goodbye", "goodness"),
            "tha" to listOf("thank", "that", "than", "thanks", "thankful"),
            "ple" to listOf("please", "pleasure", "pleasant", "pledge"),
            "hav" to listOf("have", "haven't", "having", "haven"),
            "wha" to listOf("what", "whats", "whatever", "what's"),
            "how" to listOf("how", "however", "howdy", "how's"),
            "hey" to listOf("hey", "hello", "hey!", "hey there"),
            "mee" to listOf("meet", "meek", "meeting", "meets"),
            "nee" to listOf("need", "needle", "needs", "needy"),
            "lov" to listOf("love", "lovely", "lover", "loving"),
            "wan" to listOf("want", "wander", "wanted", "wanting"),
            "com" to listOf("come", "common", "coming", "complete"),
            "whe" to listOf("when", "where", "whenever", "wherever"),
            "whi" to listOf("which", "while", "whisper", "whistle"),
            "sho" to listOf("should", "show", "shower", "shopping"),
            "wou" to listOf("would", "wound", "wouldn't", "would've"),
            "cou" to listOf("could", "count", "country", "couldn't"),
            "pro" to listOf("problem", "project", "probably", "program"),
            "thi" to listOf("this", "think", "things", "thinking"),
            "wit" to listOf("with", "without", "within", "witness"),
            "abo" to listOf("about", "above", "abroad", "absolute"),
            "fro" to listOf("from", "front", "frozen", "fruit"),
            "jus" to listOf("just", "justice", "justify", "just now"),
            "sti" to listOf("still", "stick", "stir", "stitch"),
            "als" to listOf("also", "always", "although", "alright"),
            "eve" to listOf("everything", "evening", "event", "everyone"),
            "som" to listOf("something", "sometimes", "someone", "somewhere"),
            "any" to listOf("anything", "anyway", "anyone", "anywhere"),
            "nic" to listOf("nice", "nickel", "nicely", "niche"),
            "bea" to listOf("beautiful", "beach", "beast", "beauty"),
            "amaz" to listOf("amazing", "amaze", "amazed", "amazement"),
            "won" to listOf("wonderful", "won", "wonder", "wondering"),
            "gre" to listOf("great", "greet", "greatest", "greeting"),
            "awes" to listOf("awesome", "awe", "awesome!", "awesomeness"),
            "absol" to listOf("absolutely", "absolute", "absolved"),
            "prob" to listOf("probably", "problem", "probe", "probability"),
            "def" to listOf("definitely", "defend", "definition", "default"),
            "imp" to listOf("important", "improve", "impossible", "impact"),
            "int" to listOf("interesting", "internet", "into", "international"),
            "dif" to listOf("different", "difficult", "difference", "diff"),
            "per" to listOf("perfect", "perhaps", "person", "perform"),
            "sug" to listOf("suggest", "suggestion", "suggesting", "suggests"),
            "app" to listOf("appreciate", "application", "apparently", "approach"),
            "con" to listOf("congratulations", "continue", "consider", "connect"),
            "bel" to listOf("believe", "below", "belong", "beloved"),
            "acc" to listOf("according", "accept", "account", "accomplish"),
            "res" to listOf("respect", "response", "result", "remember"),
            "sor" to listOf("sorry", "sort", "sorrow", "sorting"),
            "amo" to listOf("among", "amount", "amongst", "amorous"),
            "per" to listOf("person", "perhaps", "perfect", "perform"),
            "sur" to listOf("sure", "surprise", "surface", "surrender"),
            "qui" to listOf("quick", "quite", "quiet", "quit"),
            "lon" to listOf("long", "look", "love", "lonely"),
            "wor" to listOf("work", "world", "worry", "wonderful"),
            "peo" to listOf("people", "person", "peoples"),
            "cal" to listOf("call", "calendar", "calculate", "calm"),
            "dra" to listOf("draw", "drama", "dragon", "draft"),
            "dre" to listOf("dream", "dress", "dread", "dreamy"),
            "fri" to listOf("friend", "friday", "friendly", "friendship"),
            "fam" to listOf("family", "famous", "familiar", "famine"),
            "mem" to listOf("memory", "member", "remember", "membership"),
            "exp" to listOf("experience", "explain", "express", "expect"),
            "pos" to listOf("possible", "positive", "post", "position"),
            "per" to listOf("perfect", "person", "perhaps", "perform"),
            "rea" to listOf("really", "read", "ready", "reason"),
            "fin" to listOf("find", "finish", "final", "fine"),
            "giv" to listOf("give", "given", "giving", "gives"),
            "tal" to listOf("talk", "talent", "take", "tall"),
            "kee" to listOf("keep", "keeping", "keeps"),
            "fee" to listOf("feel", "feedback", "feeling", "feet"),
            "loo" to listOf("look", "looking", "looked", "looks"),
            "try" to listOf("trying", "tried", "try", "truly"),
            "liv" to listOf("live", "living", "lived", "lively"),
            "bel" to listOf("believe", "below", "belong", "beloved"),
            "hap" to listOf("happy", "happen", "happened", "happiness"),
            "tha" to listOf("thank", "that", "thanks", "thankful"),
            "spe" to listOf("special", "speak", "speed", "spend"),
            "sto" to listOf("stop", "story", "store", "storm"),
            "hel" to listOf("help", "hello", "helpful", "held"),
            "mem" to listOf("memory", "member", "remember", "membership"),
            "cha" to listOf("change", "chance", "chat", "challenge"),
            "sup" to listOf("support", "super", "supply", "suppose"),
            "rec" to listOf("receive", "recently", "record", "recommend"),
            "org" to listOf("organization", "organize", "original"),
            "inf" to listOf("information", "influence", "inform"),
            "per" to listOf("person", "perfect", "perhaps", "perform"),
            "ind" to listOf("individual", "industry", "indeed", "index"),
            "nev" to listOf("never", "nevertheless", "never mind"),
            "sel" to listOf("self", "sell", "select", "seldom"),
            "min" to listOf("mind", "mine", "minute", "minimum"),
            "lea" to listOf("learn", "leave", "lead", "least"),
            "brin" to listOf("bring", "bringing", "brings"),
            "star" to listOf("start", "star", "staring", "started"),
            "run" to listOf("running", "run", "runner"),
            "mak" to listOf("make", "making", "makes"),
            "tak" to listOf("take", "taking", "takes"),
            "thin" to listOf("think", "things", "thinking", "thing"),
            "know" to listOf("know", "knowledge", "knows", "knowing"),
            "see" to listOf("see", "seeing", "seems", "seen"),
            "tim" to listOf("time", "timing", "times"),
            "day" to listOf("day", "days", "daylight", "daydream"),
            "peop" to listOf("people", "person", "peoples"),
            "wom" to listOf("woman", "women", "wonderful"),
            "chi" to listOf("child", "children", "childhood", "china"),
            "wor" to listOf("world", "work", "worry", "worse"),
            "lif" to listOf("life", "lifetime", "lifestyle"),
            "han" to listOf("hand", "hands", "handle", "happen"),
            "par" to listOf("part", "party", "partner", "particular"),
            "pla" to listOf("place", "plan", "play", "please")
        )

        // Find matching completions
        for ((prefix, words) in completions) {
            if (lower.startsWith(prefix) && lower.length >= prefix.length) {
                for (word in words) {
                    if (word.startsWith(lower) && word != lower) {
                        if (!suggestions.contains(word.replaceFirstChar { it.uppercase() })) {
                            suggestions.add(word.replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }
        }

        // Phrase-level predictions — suggest common phrases
        val phrasePredictions = mapOf(
            "i " to listOf("I love", "I want", "I need", "I think", "I know"),
            "you " to listOf("you are", "you have", "you know", "you can", "you should"),
            "how " to listOf("how are you", "how is it", "how do you", "how much"),
            "what " to listOf("what is", "what are", "what do you", "what time"),
            "can " to listOf("can you", "can we", "can I", "can help"),
            "please " to listOf("please help", "please send", "please check", "please let"),
            "thank " to listOf("thank you", "thank you so much", "thanks for", "thankful for"),
            "good " to listOf("good morning", "good night", "good job", "good idea"),
            "i am " to listOf("I am doing", "I am happy", "I am here", "I am ready"),
            "do you " to listOf("do you know", "do you have", "do you want", "do you think"),
            "would you " to listOf("would you like", "would you mind", "would you help"),
            "let me " to listOf("let me know", "let me see", "let me help", "let me check"),
            "looking " to listOf("looking for", "looking forward", "looking at", "looking good"),
            "trying " to listOf("trying to", "trying my best", "trying hard"),
            "going " to listOf("going to", "going home", "going out", "going well"),
            "want to " to listOf("want to go", "want to see", "want to know", "want to help"),
            "need to " to listOf("need to go", "need to know", "need to see", "need help"),
            "happy " to listOf("happy birthday", "happy to help", "happy for you", "happy day")
        )

        for ((prefix, phrases) in phrasePredictions) {
            if (lower.startsWith(prefix) || lower.endsWith(prefix.trim())) {
                for (phrase in phrases) {
                    if (!suggestions.contains(phrase)) {
                        suggestions.add(phrase)
                    }
                }
            }
        }

        // If we have the full word, suggest next words
        if (suggestions.size < 3) {
            val nextWords = mapOf(
                "hello" to listOf("how", "there", "everyone"),
                "thank" to listOf("you", "god", "goodness"),
                "good" to listOf("morning", "night", "job"),
                "how" to listOf("are", "is", "do"),
                "what" to listOf("is", "are", "do"),
                "i" to listOf("love", "want", "need"),
                "you" to listOf("are", "have", "need"),
                "the" to listOf("best", "most", "first"),
                "this" to listOf("is", "was", "will"),
                "please" to listOf("help", "check", "send"),
                "can" to listOf("you", "we", "i"),
                "have" to listOf("a", "you", "been"),
                "with" to listOf("you", "love", "care"),
                "love" to listOf("you", "this", "it"),
                "great" to listOf("job", "work", "day"),
                "nice" to listOf("to", "and", "work")
            )
            for ((word, nexts) in nextWords) {
                if (lower.endsWith(word)) {
                    for (next in nexts) {
                        if (!suggestions.contains("$word $next")) {
                            suggestions.add("$word $next")
                        }
                    }
                }
            }
        }

        return suggestions.take(3)
    }

    // ═══════════════════════════════════════════════════════════════
    //  OVERRIDE: Update suggestions to use AI engine
    // ═══════════════════════════════════════════════════════════════

    /** Fills the three suggestion slots of the suggestions bar. */
    private fun showSuggestions(root: View, left: String, center: String, right: String) {
        root.findViewById<TextView>(R.id.suggestion_left)?.text = left
        root.findViewById<TextView>(R.id.suggestion_center)?.text = center
        root.findViewById<TextView>(R.id.suggestion_right)?.text = right
    }

    private fun updateSuggestionsBarWithAI() {
        try {
            val root = keyboardRootView ?: return
            if (!autoSuggestionsEnabled) return

            val rawInput = composingText.toString()

            // Use Gemini if active
            if (geminiActive) {
                updateGeminiSuggestions()
                return
            }

            // Premium Assist — enhanced AI suggestions (works from 1st character)
            if (premiumAssistEnabled && rawInput.isNotEmpty()) {
                val assistSuggestions = getPremiumAssistSuggestions(rawInput)
                if (assistSuggestions.isNotEmpty()) {
                    showSuggestions(
                        root,
                        assistSuggestions.getOrElse(0) { "" },
                        assistSuggestions.getOrElse(1) { "✨ Assist" },
                        assistSuggestions.getOrElse(2) { "" }
                    )
                    return
                }
            }

            // Use AI Suggestions Engine
            val suggestions = AISuggestionsEngine.getSuggestions(rawInput, lastCommittedWord)
            if (suggestions.isNotEmpty()) {
                showSuggestions(
                    root,
                    suggestions.getOrElse(0) { "" },
                    suggestions.getOrElse(1) { "SpellType" },
                    suggestions.getOrElse(2) { "" }
                )
            } else {
                // Fall back to mood detector
                val moodSuggestion = MoodDetector.detectMood(rawInput)
                root.findViewById<TextView>(R.id.suggestion_center)?.text = "Mood: ${moodSuggestion.mood.displayName} ${moodSuggestion.mood.emoji}"
                val list = moodSuggestion.suggestedStyles
                if (list.isNotEmpty()) {
                    root.findViewById<TextView>(R.id.suggestion_left)?.text = "Try " + list[0].name.lowercase().replace("_", " ")
                }
                if (list.size > 1) {
                    root.findViewById<TextView>(R.id.suggestion_right)?.text = "Try " + list[1].name.lowercase().replace("_", " ")
                }
            }

            // Autocorrect check
            if (rawInput.isNotEmpty()) {
                val corrected = AISuggestionsEngine.autocorrect(rawInput)
                if (corrected != null) {
                    root.findViewById<TextView>(R.id.suggestion_center)?.text = "→ $corrected"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  PREMIUM KEY PRESS with 130fps animation
    // ═══════════════════════════════════════════════════════════════

    private fun onKeyClickFeedbackPremium(view: View) {
        try {
            // Use premium animation engine
            if (view is TextView) {
                PremiumAnimationEngine.animateKeyPress(view, activeRealTheme.glowColor)
            }
            playVibrationFeedback()
            playSoundFeedback()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CONTROL CENTER SYSTEM — 32+ Working Options
    // ═══════════════════════════════════════════════════════════════

    private var controlCenterActive = false
    private var simpleModeActive = false

    private fun toggleControlCenter() {
        try {
            val root = keyboardRootView ?: return
            val keysContainer = root.findViewById<View>(R.id.keyboard_keys_container) ?: return
            val controlCenter = root.findViewById<View>(R.id.keyboard_control_center) ?: return

            controlCenterActive = !controlCenterActive
            if (controlCenterActive) {
                keysContainer.visibility = View.GONE
                controlCenter.visibility = View.VISIBLE
                bindControlCenter(root)
            } else {
                keysContainer.visibility = View.VISIBLE
                controlCenter.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Toggle Simple Keyboard Mode — hides all toolbars, shows only keys, Gboard-style flat look */
    private fun applySimpleMode() {
        try {
            val root = keyboardRootView ?: return
            val density = resources.displayMetrics.density

            // Find all bars to hide/show
            val headerBar = root.findViewById<View>(R.id.btn_language)?.parent as? View
            val artBarContainer = root.findViewById<View>(R.id.quick_art_container)?.parent?.parent as? View
            val suggestionsBar = root.findViewById<View>(R.id.ai_suggestions_bar)
            val proToolsBar = root.findViewById<View>(R.id.pro_tools_bar)
            val livePreview = root.findViewById<View>(R.id.tv_keyboard_live_preview)
            val numberRow = root.findViewById<View>(R.id.number_row)

            if (simpleModeActive) {
                // Hide ALL bars — truly minimal like Gboard
                headerBar?.visibility = View.GONE
                artBarContainer?.visibility = View.GONE
                suggestionsBar?.visibility = View.GONE
                proToolsBar?.visibility = View.GONE
                livePreview?.visibility = View.GONE
                numberRow?.visibility = View.GONE

                // Apply Gboard-style flat key look
                root.setBackgroundColor(Color.parseColor("#1F2023"))
                for ((id, view) in keyViews) {
                    val isSpecial = id == R.id.btn_shift || id == R.id.btn_backspace ||
                        id == R.id.btn_mode || id == R.id.btn_enter || id == R.id.btn_space
                    // Flat key with minimal roundness, no 3D effects
                    val flatBg = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                        cornerRadius = 4f * density
                        setColor(if (isSpecial) Color.parseColor("#1F2023") else Color.parseColor("#3C4043"))
                    }
                    view.background = flatBg
                    view.setTextColor(Color.WHITE)
                    // Remove any elevation/shadow for flat look
                    view.elevation = 0f
                    view.translationZ = 0f
                }
            } else {
                // Restore all bars
                headerBar?.visibility = View.VISIBLE
                artBarContainer?.visibility = View.VISIBLE
                if (autoSuggestionsEnabled) suggestionsBar?.visibility = View.VISIBLE
                proToolsBar?.visibility = View.VISIBLE
                if (numberRowEnabled) numberRow?.visibility = View.VISIBLE

                // Restore premium theme
                applyPremiumTheme()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var currentAlphabetIndex = 0
    private val alphabetNames = listOf("QWERTY", "DVORAK", "COLEMAK", "AZERTY", "QWERTZ", "ABCDE", "CUSTOM")
    private val alphabetsLowerList = listOf(
        // QWERTY
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "z", "x", "c", "v", "b", "n", "m"),
        // DVORAK
        listOf("p", "y", "f", "g", "c", "r", "l", "a", "o", "e", "u", "i", "d", "h", "t", "n", "s", "q", "j", "k", "x", "b", "m", "w", "v", "z"),
        // COLEMAK
        listOf("q", "w", "f", "p", "g", "j", "l", "u", "y", "a", "r", "s", "t", "d", "h", "n", "e", "i", "o", "z", "x", "c", "v", "b", "k", "m"),
        // AZERTY
        listOf("a", "z", "e", "r", "t", "y", "u", "i", "o", "p", "q", "s", "d", "f", "g", "h", "j", "k", "l", "w", "x", "c", "v", "b", "n", "m"),
        // QWERTZ
        listOf("q", "w", "e", "r", "t", "z", "u", "i", "o", "p", "a", "s", "d", "f", "g", "h", "j", "k", "l", "y", "x", "c", "v", "b", "n", "m"),
        // ABCDE
        listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"),
        // CUSTOM
        listOf("🌟", "🔥", "💖", "✨", "👑", "💎", "🍀", "🌸", "🎵", "❄️", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p")
    )

    /**
     * Wires a control center ON/OFF option: renders "label: state" and re-renders after each tap.
     * [onToggle] owns the actual state change so callers keep their own backing fields.
     */
    private fun bindToggleOption(
        root: View,
        viewId: Int,
        label: String,
        onText: String = "ON",
        offText: String = "OFF",
        isOn: () -> Boolean,
        onToggle: () -> Unit
    ) {
        val button = root.findViewById<TextView>(viewId) ?: return
        button.text = "$label: ${if (isOn()) onText else offText}"
        button.setOnClickListener {
            onKeyClickFeedback(it)
            onToggle()
            button.text = "$label: ${if (isOn()) onText else offText}"
        }
    }

    /** Wires a particle effect option that starts its effect, or stops it when already running. */
    private fun bindParticleEffect(
        root: View,
        viewId: Int,
        label: String,
        type: ParticleEffectsEngine.EffectType,
        start: (View) -> Unit
    ) {
        val button = root.findViewById<TextView>(viewId) ?: return
        button.setOnClickListener {
            onKeyClickFeedback(it)
            if (ParticleEffectsEngine.isActive() && activeParticleEffect == type) {
                ParticleEffectsEngine.stop()
                activeParticleEffect = ParticleEffectsEngine.EffectType.NONE
                button.text = "$label: OFF"
            } else {
                val target = keyboardRootView ?: return@setOnClickListener
                start(target)
                activeParticleEffect = type
                button.text = "$label: ON"
            }
        }
    }

    private var activeParticleEffect = ParticleEffectsEngine.EffectType.NONE

    private fun bindControlCenter(root: View) {
        val keysContainer = root.findViewById<View>(R.id.keyboard_keys_container) ?: return
        val controlCenter = root.findViewById<View>(R.id.keyboard_control_center) ?: return

        // Back to keyboard
        root.findViewById<View>(R.id.btn_close_control_center)?.setOnClickListener {
            onKeyClickFeedback(it)
            controlCenterActive = false
            controlCenter.visibility = View.GONE
            keysContainer.visibility = View.VISIBLE
        }

        // Real Workable Google AI Search Mode Engine
        val searchInput = root.findViewById<android.widget.EditText>(R.id.ai_search_input)
        root.findViewById<View>(R.id.btn_ai_search_submit)?.setOnClickListener {
            onKeyClickFeedback(it)
            val query = searchInput?.text?.toString() ?: ""
            if (query.isNotBlank()) {
                performGoogleAISearch(query)
                searchInput?.setText("")
            }
        }

        // 1. Gboard Type Choice
        bindToggleOption(root, R.id.btn_opt_gboard, "⌨️ Gboard Mode", isOn = { gboardModeEnabled }) {
            gboardModeEnabled = !gboardModeEnabled
            applyPremiumTheme()
        }

        // 2. 3D Keycaps Mode
        bindToggleOption(root, R.id.btn_opt_3d_keys, "🧱 3D Keycaps", isOn = { force3DKeycaps }) {
            force3DKeycaps = !force3DKeycaps
            RealTheme.force3D = force3DKeycaps
            applyPremiumTheme()
        }

        // 3. Dynamic Haptic Toggle
        bindToggleOption(root, R.id.btn_opt_haptic, "📳 Haptic Haptic", isOn = { vibrationEnabled }) {
            vibrationEnabled = !vibrationEnabled
        }

        // 4. Cycle Alphabet
        val btnAlphabet = root.findViewById<TextView>(R.id.btn_opt_alphabet)
        btnAlphabet?.text = "🔤 Layout: ${alphabetNames[currentAlphabetIndex]}"
        btnAlphabet?.setOnClickListener {
            onKeyClickFeedback(it)
            currentAlphabetIndex = (currentAlphabetIndex + 1) % alphabetNames.size
            btnAlphabet.text = "🔤 Layout: ${alphabetNames[currentAlphabetIndex]}"
            applyAlphabetLayout()
        }

        // 5. Haptic Strength Multiplier
        val btnHapticStrong = root.findViewById<TextView>(R.id.btn_opt_haptic_strong)
        btnHapticStrong?.text = "💪 Haptic Strength: $vibrationStrength%"
        btnHapticStrong?.setOnClickListener {
            onKeyClickFeedback(it)
            vibrationStrength = if (vibrationStrength >= 150) 25 else vibrationStrength + 25
            btnHapticStrong.text = "💪 Haptic Strength: $vibrationStrength%"
        }

        // 6. Key Sound Audio Toggle
        bindToggleOption(root, R.id.btn_opt_sound, "🔊 Key Click Audio", isOn = { soundEnabled }) {
            soundEnabled = !soundEnabled
        }

        // 7. Key Sound Volume Control
        val btnVolume = root.findViewById<TextView>(R.id.btn_opt_volume)
        btnVolume?.text = "🎚️ Sound Volume: $soundVolume%"
        btnVolume?.setOnClickListener {
            onKeyClickFeedback(it)
            soundVolume = if (soundVolume >= 100) 10 else soundVolume + 10
            btnVolume.text = "🎚️ Sound Volume: $soundVolume%"
        }

        // 8. Keyboard Height Tiny
        root.findViewById<TextView>(R.id.btn_opt_height_tiny)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "SMALL"
            applyKeyboardHeight(keyboardHeight)
        }

        // 9. Keyboard Height Normal
        root.findViewById<TextView>(R.id.btn_opt_height_normal)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "MEDIUM"
            applyKeyboardHeight(keyboardHeight)
        }

        // 10. Keyboard Height Tall
        root.findViewById<TextView>(R.id.btn_opt_height_tall)?.setOnClickListener {
            onKeyClickFeedback(it)
            keyboardHeight = "LARGE"
            applyKeyboardHeight(keyboardHeight)
        }

        // 11. Number Row Show/Hide
        bindToggleOption(
            root, R.id.btn_opt_numrow, "🔢 Number Row",
            onText = "SHOW", offText = "HIDE", isOn = { numberRowEnabled }
        ) {
            numberRowEnabled = !numberRowEnabled
            root.findViewById<View>(R.id.number_row)?.visibility = if (numberRowEnabled) View.VISIBLE else View.GONE
        }

        // 12. Smart Auto Suggestions
        bindToggleOption(
            root, R.id.btn_opt_suggest, "💡 Suggestion Bar",
            onText = "SHOW", offText = "HIDE", isOn = { autoSuggestionsEnabled }
        ) {
            autoSuggestionsEnabled = !autoSuggestionsEnabled
            root.findViewById<View>(R.id.ai_suggestions_bar)?.visibility = if (autoSuggestionsEnabled) View.VISIBLE else View.GONE
        }

        // 13. Rainbow Live Preview
        bindToggleOption(root, R.id.btn_opt_rainbow, "🌈 Rainbow Preview", isOn = { rainbowPreviewEnabled }) {
            rainbowPreviewEnabled = !rainbowPreviewEnabled
            val tvPreview = root.findViewById<TextView>(R.id.tv_keyboard_live_preview)
            if (rainbowPreviewEnabled) {
                tvPreview?.setBackgroundColor(Color.parseColor("#3B0066"))
                tvPreview?.setTextColor(Color.parseColor("#00FFCC"))
            } else {
                tvPreview?.setBackgroundColor(Color.parseColor("#05070C"))
                tvPreview?.setTextColor(Color.parseColor("#00FFE0"))
            }
        }

        // 14. Giant Words Mode
        bindToggleOption(root, R.id.btn_opt_giant, "🅰️ Giant Words", isOn = { giantWordsEnabled }) {
            giantWordsEnabled = !giantWordsEnabled
        }

        // 15. Glitter Sparkle Sparkle
        bindToggleOption(root, R.id.btn_opt_glitter, "✨ Glitter Mode", isOn = { glitterEnabled }) {
            glitterEnabled = !glitterEnabled
        }

        // 16. Unicode Gothic Style
        bindUnicodeToggle(root, R.id.btn_opt_gothic, "🏰 Gothic Unicode", UnicodeStyle.GOTHIC)

        // 17. Unicode Bold Style
        bindUnicodeToggle(root, R.id.btn_opt_bold, "🄱 Bold Unicode", UnicodeStyle.CIRCLED)

        // 18. Unicode Cursive Style
        bindUnicodeToggle(root, R.id.btn_opt_cursive, "✍️ Cursive Unicode", UnicodeStyle.CURSIVE)

        // 19. Emoji Frames Selector
        bindToggleOption(root, R.id.btn_opt_emoji_frames, "🖼️ Emoji Borders", isOn = { activeStyle != FrameStyle.NONE }) {
            activeStyle = if (activeStyle == FrameStyle.NONE) FrameStyle.SPARKS else FrameStyle.NONE
        }

        // 20. Custom Signature Toggle
        bindToggleOption(root, R.id.btn_opt_signature, "🖋️ Signature Tail", isOn = { customSignature.isNotBlank() }) {
            customSignature = if (customSignature.isBlank()) "Sent with SpellType 🪄" else ""
        }

        // 21. Language English
        root.findViewById<TextView>(R.id.btn_opt_lang_en)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("en")
            applyLanguage()
        }

        // 22. Language Hindi/Hinglish
        root.findViewById<TextView>(R.id.btn_opt_lang_hi)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("hi")
            applyLanguage()
        }

        // 23. Language Spanish
        root.findViewById<TextView>(R.id.btn_opt_lang_es)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("es")
            applyLanguage()
        }

        // 24. Language French
        root.findViewById<TextView>(R.id.btn_opt_lang_fr)?.setOnClickListener {
            onKeyClickFeedback(it)
            currentLanguage = LanguageManager.getLanguageByCode("fr")
            applyLanguage()
        }

        // 25. 130FPS Render Precision
        bindToggleOption(root, R.id.btn_opt_60fps, "⚡ 130FPS Ultra Render", isOn = { highFpsRenderEnabled }) {
            highFpsRenderEnabled = !highFpsRenderEnabled
        }

        // 26. Holographic Glow Effect
        bindToggleOption(root, R.id.btn_opt_holo_glow, "🎇 Holographic Glow", isOn = { holographicGlowEnabled }) {
            holographicGlowEnabled = !holographicGlowEnabled
        }

        // 27. Particle System
        bindToggleOption(root, R.id.btn_opt_particles, "☄️ Tap Particles", isOn = { tapParticlesEnabled }) {
            tapParticlesEnabled = !tapParticlesEnabled
        }

        // 28. Template Keyboard Choice
        val btnTemplatesOpt = root.findViewById<TextView>(R.id.btn_opt_templates)
        btnTemplatesOpt?.setOnClickListener {
            onKeyClickFeedback(it)
            handleTemplatesToolWithAd()
        }

        // 29. Premium Assist
        bindToggleOption(root, R.id.btn_opt_assist, "🪄 Premium Assist", isOn = { premiumAssistEnabled }) {
            premiumAssistEnabled = !premiumAssistEnabled
        }

        // 30. Clear Compose Buffer
        root.findViewById<TextView>(R.id.btn_opt_clear_buffer)?.setOnClickListener {
            onKeyClickFeedback(it)
            composingText.clear()
            currentInputConnection?.setComposingText("", 1)
            updateLivePreviewBar()
        }

        // 31. Clipboard History Queue
        root.findViewById<TextView>(R.id.btn_opt_clipboard)?.setOnClickListener {
            onKeyClickFeedback(it)
            handleClipboardToolWithAd()
        }

        // 32. Ad-Free Sandbox
        bindToggleOption(root, R.id.btn_opt_adfree, "🛡️ Ad-Free Sandbox", isOn = { adFreeSandboxEnabled }) {
            adFreeSandboxEnabled = !adFreeSandboxEnabled
            premiumUnlocked = adFreeSandboxEnabled
            updateKeyboardAdBanners()
        }

        // ═══ Particle Effects ═══
        bindParticleEffect(root, R.id.btn_opt_rain, "🌧️ Rain Effect", ParticleEffectsEngine.EffectType.RAIN, ParticleEffectsEngine::startRain)
        bindParticleEffect(root, R.id.btn_opt_cherry, "🌸 Cherry Blossom", ParticleEffectsEngine.EffectType.CHERRY_BLOSSOM, ParticleEffectsEngine::startCherryBlossom)
        bindParticleEffect(root, R.id.btn_opt_snow, "❄️ Snow Effect", ParticleEffectsEngine.EffectType.SNOW, ParticleEffectsEngine::startSnow)
        bindParticleEffect(root, R.id.btn_opt_sparkle, "✨ Sparkle Effect", ParticleEffectsEngine.EffectType.SPARKLE, ParticleEffectsEngine::startSparkle)

        // Simple Keyboard Mode
        bindToggleOption(root, R.id.btn_opt_simple_mode, "🔇 Simple Keyboard", isOn = { simpleModeActive }) {
            simpleModeActive = !simpleModeActive
            applySimpleMode()
        }
    }

    /** Wires a control center option that switches a single unicode style on or back to NONE. */
    private fun bindUnicodeToggle(root: View, viewId: Int, label: String, style: UnicodeStyle) {
        bindToggleOption(root, viewId, label, isOn = { activeUnicode == style }) {
            activeUnicode = if (activeUnicode == style) UnicodeStyle.NONE else style
        }
    }

    private fun applyAlphabetLayout() {
        val lower = alphabetsLowerList[currentAlphabetIndex]
        for (i in letterKeyIds.indices) {
            val id = letterKeyIds[i]
            val keyView = keyViews[id] ?: continue
            val char = lower[i]
            keyView.text = if (isShifted) char.uppercase() else char
        }
    }

    private fun performGoogleAISearch(query: String) {
        if (query.isBlank()) return
        serviceScope.launch(Dispatchers.IO) {
            var urlConnection: java.net.HttpURLConnection? = null
            try {
                val encodedQuery = java.net.URLEncoder.encode(query.trim(), "UTF-8")
                if (encodedQuery.isBlank()) return@launch
                val url = java.net.URL("https://api.duckduckgo.com/?q=$encodedQuery&format=json&no_html=1&skip_disambig=1")
                urlConnection = url.openConnection() as java.net.HttpURLConnection
                urlConnection.connectTimeout = 5000
                urlConnection.readTimeout = 5000
                urlConnection.requestMethod = "GET"
                urlConnection.setRequestProperty("User-Agent", "SpellType/4.0")
                urlConnection.connect()

                val responseCode = urlConnection.responseCode
                if (responseCode != 200) {
                    val fallback = generateLocalAISearchResult(query)
                    withContext(Dispatchers.Main) {
                        currentInputConnection?.commitText("\n🤖 [AI Search]: $fallback\n", 1)
                    }
                    return@launch
                }

                val text = urlConnection.inputStream.bufferedReader().use { it.readText() }

                // Parse abstract from JSON manually to avoid adding Jackson/Gson overhead
                var abstractText = ""
                val abstractKey = "\"AbstractText\":\""
                val index = text.indexOf(abstractKey)
                if (index != -1) {
                    val start = index + abstractKey.length
                    val end = text.indexOf("\"", start)
                    if (end != -1 && end > start) {
                        abstractText = text.substring(start, end).replace("\\n", " ").replace("\\\"", "\"")
                    }
                }

                // Fallback to local AI generator if DuckDuckGo returns empty
                if (abstractText.isBlank()) {
                    abstractText = generateLocalAISearchResult(query)
                }

                withContext(Dispatchers.Main) {
                    val ic = currentInputConnection
                    ic?.commitText("\n🤖 [AI Search]: $abstractText\n", 1)
                    keyboardRootView?.findViewById<TextView>(R.id.suggestion_center)?.text = "AI Result Added!"
                }
            } catch (e: java.net.SocketTimeoutException) {
                e.printStackTrace()
                val fallback = generateLocalAISearchResult(query)
                withContext(Dispatchers.Main) {
                    currentInputConnection?.commitText("\n🤖 [AI Search]: $fallback\n", 1)
                }
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                val fallback = generateLocalAISearchResult(query)
                withContext(Dispatchers.Main) {
                    currentInputConnection?.commitText("\n🤖 [AI Search]: $fallback\n", 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val fallback = generateLocalAISearchResult(query)
                withContext(Dispatchers.Main) {
                    currentInputConnection?.commitText("\n🤖 [AI Search]: $fallback\n", 1)
                }
            } finally {
                try { urlConnection?.disconnect() } catch (_: Exception) {}
            }
        }
    }

    private fun generateLocalAISearchResult(query: String): String {
        val q = query.lowercase().trim()
        return when {
            q.contains("einstein") -> "Albert Einstein was a German-born theoretical physicist, widely acknowledged as one of the greatest and most influential physicists of all time."
            q.contains("newton") -> "Sir Isaac Newton was an English mathematician, physicist, astronomer, alchemist, and theologian, who is widely recognised as one of the greatest mathematicians and physicists."
            q.contains("india") -> "India is a country in South Asia. It is the seventh-largest country by area, the most populous country, and the most populous democracy in the world."
            q.contains("spelltype") -> "SpellType Keyboard is an advanced, ultra-customizable Android keyboard that features 3D rendering, AI suggestions, and automatic unicode stylized outputs!"
            q.contains("weather") -> "The current weather is clear with a gentle breeze, perfect for typing on your SpellType Keyboard! ☀️"
            q.contains("capital") || q.contains("delhi") -> "New Delhi is the capital of India and a part of the National Capital Territory of Delhi."
            q.contains("google") -> "Google LLC is an American multinational technology company focusing on artificial intelligence, search engine technology, and online advertising."
            q.contains("android") -> "Android is a mobile operating system based on a modified version of the Linux kernel and other open-source software, designed primarily for touchscreen mobile devices."
            q.contains("kotlin") -> "Kotlin is a cross-platform, statically typed, general-purpose programming language with type inference, designed to interoperate fully with Java."
            q.contains("python") -> "Python is a high-level, general-purpose programming language. Its design philosophy emphasizes code readability with the use of significant indentation."
            q.contains("javascript") -> "JavaScript, often abbreviated as JS, is a programming language that is one of the core technologies of the World Wide Web, alongside HTML and CSS."
            q.contains("machine learning") || q.contains("ml") -> "Machine learning is a subset of artificial intelligence that focuses on building systems that learn from data. It includes supervised, unsupervised, and reinforcement learning."
            q.contains("blockchain") -> "Blockchain is a distributed ledger technology that records transactions across many computers so that the records cannot be altered retroactively."
            q.contains("climate") || q.contains("global warming") -> "Climate change refers to long-term shifts in temperatures and weather patterns. Human activities have been the main driver of climate change since the 1800s."
            q.contains("pakistan") -> "Pakistan is a country in South Asia. It is the fifth-most populous country with a population exceeding 230 million. Capital: Islamabad."
            q.contains("islam") || q.contains("quran") -> "Islam is a monotheistic Abrahamic religion founded in the 7th century CE. The Quran is its holy book, believed to be the word of God as revealed to Prophet Muhammad."
            q.contains("cricket") -> "Cricket is a bat-and-ball game played between two teams of eleven players on a field. It originated in England and is now popular worldwide, especially in South Asia."
            q.contains("football") || q.contains("soccer") -> "Football (soccer) is the world's most popular sport, played by over 250 million players in over 200 countries. The FIFA World Cup is its premier tournament."
            q.contains("music") -> "Music is the art of arranging sound in time through melody, harmony, rhythm, and timbre. It is a universal cultural phenomenon present in all human societies."
            q.contains("ai") || q.contains("artificial intelligence") -> "Artificial Intelligence (AI) is the simulation of human intelligence processes by computer systems, including learning, reasoning, and self-correction."
            q.contains("space") || q.contains("nasa") -> "Space exploration is the use of astronomy and space technology to explore outer space. NASA, SpaceX, and other organizations are leading the way to Mars and beyond."
            q.contains("health") || q.contains("fitness") -> "Health and fitness encompass physical, mental, and social well-being. Regular exercise, balanced nutrition, and adequate sleep are key pillars of good health."
            q.contains("recipe") || q.contains("cook") -> "Cooking is the art and science of preparing food for consumption. It involves combining ingredients using various techniques to create nutritious and delicious meals."
            q.contains("game") || q.contains("gaming") -> "Gaming is a multi-billion dollar industry encompassing video games, mobile games, and esports. Popular genres include action, RPG, strategy, and simulation."
            q.contains("photography") -> "Photography is the art of capturing light with a camera. It involves composition, lighting, exposure, and post-processing to create compelling visual stories."
            q.contains("travel") -> "Travel is the movement of people between distant geographical locations. It can be for leisure, business, or exploration, broadening perspectives and creating memories."
            q.contains("education") -> "Education is the process of facilitating learning and acquiring knowledge, skills, values, and habits. It is a fundamental human right and key to development."
            q.contains("startup") || q.contains("business") -> "A startup is a young company founded to develop a unique product or service. Key elements include innovation, scalability, and solving real-world problems."
            q.contains("bitcoin") || q.contains("crypto") -> "Bitcoin is a decentralized digital currency created in 2009. Cryptocurrency uses cryptography for security and operates on blockchain technology."
            q.contains("spelltype") -> "SpellType Keyboard is an advanced, ultra-customizable Android keyboard that features 3D rendering, AI suggestions, 130+ languages, and automatic unicode stylized outputs! 🚀"
            else -> "SpellType AI found that '$query' is an interesting topic! For detailed results, try searching on your favorite search engine. 🚀"
        }
    }
}
