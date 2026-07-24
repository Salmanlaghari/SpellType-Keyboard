package com.spelltype.keyboard.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.spelltype.keyboard.R
import com.spelltype.keyboard.databinding.ActivitySettingsBinding
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.ShapeEngine
import com.spelltype.keyboard.domain.UnicodeStylingEngine
import com.spelltype.keyboard.domain.PreviewStyler
import com.spelltype.keyboard.domain.model.FrameStyle
import com.spelltype.keyboard.domain.model.ShapeLayout
import com.spelltype.keyboard.domain.model.UnicodeStyle
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel
    private val adapter = SavedArtAdapter(
        onDeleteClick = { art -> viewModel.deleteArt(art) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = SpellTypeDatabase.getDatabase(applicationContext)
        val preferences = KeyboardPreferences(applicationContext)
        val repository = KeyboardRepositoryImpl(database.savedArtDao(), preferences)
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(repository))[SettingsViewModel::class.java]

        setupRecyclerView()
        setupListeners()
        observeState()
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupListeners() {
        // Open Art Gallery Button
        binding.btnLaunchGallery.setOnClickListener {
            val intent = Intent(this, ArtGalleryActivity::class.java)
            startActivity(intent)
        }

        // Guide Button 1: Enable settings
        binding.btnEnableSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        // Guide Button 2: Select active IME
        binding.btnSelectIme.setOnClickListener {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.showInputMethodPicker()
        }

        // Frame selection chips
        binding.settChipNone.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.NONE) }
        binding.settChipBox.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX) }
        binding.settChipBoxDouble.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX_DOUBLE) }
        binding.settChipBoxRounded.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.BOX_ROUNDED) }
        binding.settChipStar.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.STAR) }
        binding.settChipDiamond.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.DIAMOND) }
        binding.settChipFire.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.FIRE) }
        binding.settChipHearts.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.HEARTS) }
        binding.settChipSparks.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.SPARKS) }
        binding.settChipParty.setOnClickListener { viewModel.selectFrameStyle(FrameStyle.PARTY) }

        // Shape selection chips
        binding.settShapeNone.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.NONE) }
        binding.settShapePyramid.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.PYRAMID) }
        binding.settShapeHeart.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.HEART) }
        binding.settShapeDiamond.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.DIAMOND) }
        binding.settShapeZigzag.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.ZIGZAG) }
        binding.settShapeWave.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.WAVE) }
        binding.settShapeCircle.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.CIRCLE) }
        binding.settShapeLove.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.LOVE) }
        binding.settShapeRevenge.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.REVENGE) }
        binding.settShapePubg.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.PUBG) }
        binding.settShapeSocial.setOnClickListener { viewModel.selectShapeLayout(ShapeLayout.SOCIAL_MEDIA) }

        // Unicode styling chips
        binding.settUnicodeNone.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.NONE) }
        binding.settUnicodeBold.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.BOLD) }
        binding.settUnicodeItalic.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.ITALIC) }
        binding.settUnicodeGothic.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.GOTHIC) }
        binding.settUnicodeCursive.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.CURSIVE) }
        binding.settUnicodeCircled.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.CIRCLED) }
        binding.settUnicodeSquared.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.SQUARED) }
        binding.settUnicodeSquaredSolid.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.SQUARED_SOLID) }
        binding.settUnicodeBubble.setOnClickListener { viewModel.selectUnicodeStyle(UnicodeStyle.BUBBLE) }

        // Glitter Switch listener
        binding.switchGlitter.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setGlitterEnabled(isChecked)
        }

        // Sound switch listener
        binding.switchSound.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSoundEnabled(isChecked)
        }

        // Vibration switch listener
        binding.switchVibration.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveVibrationEnabled(isChecked)
        }

        // Phase 6 Switches
        binding.switchNumberRow.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveNumberRowEnabled(isChecked)
        }

        binding.switchSuggestions.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveAutoSuggestionsEnabled(isChecked)
        }

        binding.switchSwipeTyping.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveSwipeTypingEnabled(isChecked)
        }

        binding.switchColorful.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveColorfulPreviewEnabled(isChecked)
        }

        binding.switchGiantWords.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveGiantWordsEnabled(isChecked)
        }

        // Premium Wallpapers
        binding.wallNone.setOnClickListener { viewModel.saveKeyboardWallpaperPath("") }
        binding.wallOcean.setOnClickListener { viewModel.saveKeyboardWallpaperPath("OCEAN") }
        binding.wallSunset.setOnClickListener { viewModel.saveKeyboardWallpaperPath("SUNSET") }
        binding.wallMidnight.setOnClickListener { viewModel.saveKeyboardWallpaperPath("MIDNIGHT") }
        binding.wallGlass.setOnClickListener { viewModel.saveKeyboardWallpaperPath("GLASS") }

        // Wallpaper Opacity Slider
        binding.sliderWallOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvWallOpacity.text = "Background Opacity: $progress%"
                if (fromUser) viewModel.saveKeyboardWallpaperOpacity(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Key Shapes
        binding.shapeRounded.setOnClickListener { viewModel.saveKeyShape("ROUNDED") }
        binding.shapeSquare.setOnClickListener { viewModel.saveKeyShape("SQUARE") }
        binding.shapeCircular.setOnClickListener { viewModel.saveKeyShape("CIRCULAR") }
        binding.shapeGlassmorphic.setOnClickListener { viewModel.saveKeyShape("GLASSMORPHISM") }

        // Key Border Switch
        binding.switchKeyBorder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveKeyBorderEnabled(isChecked)
        }

        // Key Border Thickness
        binding.sliderBorderThickness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvBorderThickness.text = "Key Border Thickness: ${progress}dp"
                if (fromUser) viewModel.saveKeyBorderThickness(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Key Text Size Profile
        binding.txtSizeSmall.setOnClickListener { viewModel.saveKeyTextSize("SMALL") }
        binding.txtSizeMedium.setOnClickListener { viewModel.saveKeyTextSize("MEDIUM") }
        binding.txtSizeLarge.setOnClickListener { viewModel.saveKeyTextSize("LARGE") }
        binding.txtSizeHuge.setOnClickListener { viewModel.saveKeyTextSize("HUGE") }

        // Vibration Strength SeekBar
        binding.sliderVibrationStrength.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvVibrationStrength.text = "Vibration Strength: $progress%"
                if (fromUser) viewModel.saveVibrationStrength(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Sound Volume SeekBar
        binding.sliderSoundVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvSoundVolume.text = "Sound Volume: $progress%"
                if (fromUser) viewModel.saveKeySoundVolume(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Height chips
        binding.heightSmall.setOnClickListener { viewModel.saveKeyboardHeight("SMALL") }
        binding.heightMedium.setOnClickListener { viewModel.saveKeyboardHeight("MEDIUM") }
        binding.heightLarge.setOnClickListener { viewModel.saveKeyboardHeight("LARGE") }

        // Theme selection chips (extended)
        binding.themeDark.setOnClickListener { viewModel.setThemeSelection("DARK") }
        binding.themeAmoled.setOnClickListener { viewModel.setThemeSelection("AMOLED") }
        binding.themeLight.setOnClickListener { viewModel.setThemeSelection("LIGHT") }
        binding.themeBlue.setOnClickListener { viewModel.setThemeSelection("BLUE") }
        binding.themePurple.setOnClickListener { viewModel.setThemeSelection("PURPLE") }
        binding.themeGreen.setOnClickListener { viewModel.setThemeSelection("GREEN") }

        // Custom signature input listener
        binding.etCustomSignature.addTextChangedListener { text ->
            viewModel.setCustomSignature(text?.toString() ?: "")
        }

        // Live preview input listener
        binding.etPreviewInput.addTextChangedListener {
            updateLivePreview()
        }

        // Clear all history listener
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllArt()
        }

        // Close Ad Banner
        binding.btnCloseSettingsAd.setOnClickListener {
            binding.settingsAdBanner.visibility = View.GONE
            android.widget.Toast.makeText(this, "Ad hidden. Get Premium to remove all ads!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect and highlight selected frame style
                launch {
                    viewModel.selectedFrameStyle.collect { style ->
                        updateFrameHighlighting(style)
                        updateLivePreview()
                    }
                }

                // Collect and highlight selected shape layout
                launch {
                    viewModel.selectedShapeLayout.collect { shape ->
                        updateShapeHighlighting(shape)
                        updateLivePreview()
                    }
                }

                // Collect and highlight selected unicode style
                launch {
                    viewModel.selectedUnicodeStyle.collect { unicode ->
                        updateUnicodeHighlighting(unicode)
                        updateLivePreview()
                    }
                }

                // Collect and set glitter toggle state
                launch {
                    viewModel.glitterEnabled.collect { enabled ->
                        if (binding.switchGlitter.isChecked != enabled) {
                            binding.switchGlitter.isChecked = enabled
                        }
                        updateLivePreview()
                    }
                }

                // Collect and set sound state
                launch {
                    viewModel.soundEnabled.collect { enabled ->
                        if (binding.switchSound.isChecked != enabled) {
                            binding.switchSound.isChecked = enabled
                        }
                        binding.containerSoundVolume.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }

                // Collect and set vibration state
                launch {
                    viewModel.vibrationEnabled.collect { enabled ->
                        if (binding.switchVibration.isChecked != enabled) {
                            binding.switchVibration.isChecked = enabled
                        }
                        binding.containerVibrationStrength.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }

                // Phase 6 Flow Observers
                launch {
                    viewModel.colorfulPreviewEnabled.collect { enabled ->
                        if (binding.switchColorful.isChecked != enabled) {
                            binding.switchColorful.isChecked = enabled
                        }
                        updateLivePreview()
                    }
                }

                launch {
                    viewModel.giantWordsEnabled.collect { enabled ->
                        if (binding.switchGiantWords.isChecked != enabled) {
                            binding.switchGiantWords.isChecked = enabled
                        }
                        updateLivePreview()
                    }
                }

                launch {
                    viewModel.numberRowEnabled.collect { enabled ->
                        if (binding.switchNumberRow.isChecked != enabled) {
                            binding.switchNumberRow.isChecked = enabled
                        }
                    }
                }

                launch {
                    viewModel.autoSuggestionsEnabled.collect { enabled ->
                        if (binding.switchSuggestions.isChecked != enabled) {
                            binding.switchSuggestions.isChecked = enabled
                        }
                    }
                }

                launch {
                    viewModel.swipeTypingEnabled.collect { enabled ->
                        if (binding.switchSwipeTyping.isChecked != enabled) {
                            binding.switchSwipeTyping.isChecked = enabled
                        }
                    }
                }

                launch {
                    viewModel.vibrationStrength.collect { strength ->
                        if (binding.sliderVibrationStrength.progress != strength) {
                            binding.sliderVibrationStrength.progress = strength
                        }
                        binding.tvVibrationStrength.text = "Vibration Strength: $strength%"
                    }
                }

                launch {
                    viewModel.keySoundVolume.collect { volume ->
                        if (binding.sliderSoundVolume.progress != volume) {
                            binding.sliderSoundVolume.progress = volume
                        }
                        binding.tvSoundVolume.text = "Sound Volume: $volume%"
                    }
                }

                launch {
                    viewModel.keyboardHeight.collect { height ->
                        updateHeightHighlighting(height)
                    }
                }

                // Collect and highlight selected theme
                launch {
                    viewModel.themeSelection.collect { theme ->
                        updateThemeHighlighting(theme)
                    }
                }

                // Collect Premium Theme Wallpaper Profile
                launch {
                    viewModel.keyboardWallpaperPath.collect { path ->
                        updateWallpaperHighlighting(path)
                    }
                }

                launch {
                    viewModel.keyboardWallpaperOpacity.collect { opacity ->
                        if (binding.sliderWallOpacity.progress != opacity) {
                            binding.sliderWallOpacity.progress = opacity
                        }
                        binding.tvWallOpacity.text = "Background Opacity: $opacity%"
                    }
                }

                // Collect Key Shape Profile
                launch {
                    viewModel.keyShape.collect { shape ->
                        updateKeyShapeHighlighting(shape)
                    }
                }

                // Collect Key Border Enabled
                launch {
                    viewModel.keyBorderEnabled.collect { enabled ->
                        if (binding.switchKeyBorder.isChecked != enabled) {
                            binding.switchKeyBorder.isChecked = enabled
                        }
                        binding.containerBorderThickness.visibility = if (enabled) View.VISIBLE else View.GONE
                    }
                }

                // Collect Key Border Thickness
                launch {
                    viewModel.keyBorderThickness.collect { thickness ->
                        if (binding.sliderBorderThickness.progress != thickness) {
                            binding.sliderBorderThickness.progress = thickness
                        }
                        binding.tvBorderThickness.text = "Key Border Thickness: ${thickness}dp"
                    }
                }

                // Collect Key Text Size Profile
                launch {
                    viewModel.keyTextSize.collect { size ->
                        updateKeyTextSizeHighlighting(size)
                    }
                }

                // Collect custom signature text
                launch {
                    viewModel.customSignature.collect { signature ->
                        if (binding.etCustomSignature.text?.toString() != signature) {
                            binding.etCustomSignature.setText(signature)
                        }
                        updateLivePreview()
                    }
                }

                // Collect saved history list
                launch {
                    viewModel.savedArtList.collect { list ->
                        if (list.isEmpty()) {
                            binding.rvHistory.visibility = View.GONE
                            binding.tvEmptyHistory.visibility = View.VISIBLE
                        } else {
                            binding.rvHistory.visibility = View.VISIBLE
                            binding.tvEmptyHistory.visibility = View.GONE
                            adapter.submitList(list)
                        }
                    }
                }
            }
        }
    }

    private fun updateFrameHighlighting(active: FrameStyle) {
        binding.settChipNone.setBackgroundResource(if (active == FrameStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBox.setBackgroundResource(if (active == FrameStyle.BOX) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBoxDouble.setBackgroundResource(if (active == FrameStyle.BOX_DOUBLE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipBoxRounded.setBackgroundResource(if (active == FrameStyle.BOX_ROUNDED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipStar.setBackgroundResource(if (active == FrameStyle.STAR) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipDiamond.setBackgroundResource(if (active == FrameStyle.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipFire.setBackgroundResource(if (active == FrameStyle.FIRE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipHearts.setBackgroundResource(if (active == FrameStyle.HEARTS) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipSparks.setBackgroundResource(if (active == FrameStyle.SPARKS) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settChipParty.setBackgroundResource(if (active == FrameStyle.PARTY) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateShapeHighlighting(active: ShapeLayout) {
        binding.settShapeNone.setBackgroundResource(if (active == ShapeLayout.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapePyramid.setBackgroundResource(if (active == ShapeLayout.PYRAMID) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeHeart.setBackgroundResource(if (active == ShapeLayout.HEART) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeDiamond.setBackgroundResource(if (active == ShapeLayout.DIAMOND) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeZigzag.setBackgroundResource(if (active == ShapeLayout.ZIGZAG) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeWave.setBackgroundResource(if (active == ShapeLayout.WAVE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeCircle.setBackgroundResource(if (active == ShapeLayout.CIRCLE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeLove.setBackgroundResource(if (active == ShapeLayout.LOVE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeRevenge.setBackgroundResource(if (active == ShapeLayout.REVENGE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapePubg.setBackgroundResource(if (active == ShapeLayout.PUBG) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settShapeSocial.setBackgroundResource(if (active == ShapeLayout.SOCIAL_MEDIA) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateUnicodeHighlighting(active: UnicodeStyle) {
        binding.settUnicodeNone.setBackgroundResource(if (active == UnicodeStyle.NONE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeBold.setBackgroundResource(if (active == UnicodeStyle.BOLD) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeItalic.setBackgroundResource(if (active == UnicodeStyle.ITALIC) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeGothic.setBackgroundResource(if (active == UnicodeStyle.GOTHIC) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeCursive.setBackgroundResource(if (active == UnicodeStyle.CURSIVE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeCircled.setBackgroundResource(if (active == UnicodeStyle.CIRCLED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeSquared.setBackgroundResource(if (active == UnicodeStyle.SQUARED) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeSquaredSolid.setBackgroundResource(if (active == UnicodeStyle.SQUARED_SOLID) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.settUnicodeBubble.setBackgroundResource(if (active == UnicodeStyle.BUBBLE) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateHeightHighlighting(active: String) {
        binding.heightSmall.setBackgroundResource(if (active == "SMALL") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.heightMedium.setBackgroundResource(if (active == "MEDIUM") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.heightLarge.setBackgroundResource(if (active == "LARGE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateThemeHighlighting(theme: String) {
        binding.themeDark.setBackgroundResource(if (theme == "DARK") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.themeAmoled.setBackgroundResource(if (theme == "AMOLED") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.themeLight.setBackgroundResource(if (theme == "LIGHT") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.themeBlue.setBackgroundResource(if (theme == "BLUE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.themePurple.setBackgroundResource(if (theme == "PURPLE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.themeGreen.setBackgroundResource(if (theme == "GREEN") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateWallpaperHighlighting(path: String) {
        binding.wallNone.setBackgroundResource(if (path.isEmpty()) R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.wallOcean.setBackgroundResource(if (path == "OCEAN") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.wallSunset.setBackgroundResource(if (path == "SUNSET") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.wallMidnight.setBackgroundResource(if (path == "MIDNIGHT") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.wallGlass.setBackgroundResource(if (path == "GLASS") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateKeyShapeHighlighting(shape: String) {
        binding.shapeRounded.setBackgroundResource(if (shape == "ROUNDED") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.shapeSquare.setBackgroundResource(if (shape == "SQUARE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.shapeCircular.setBackgroundResource(if (shape == "CIRCULAR") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.shapeGlassmorphic.setBackgroundResource(if (shape == "GLASSMORPHISM") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateKeyTextSizeHighlighting(size: String) {
        binding.txtSizeSmall.setBackgroundResource(if (size == "SMALL") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.txtSizeMedium.setBackgroundResource(if (size == "MEDIUM") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.txtSizeLarge.setBackgroundResource(if (size == "LARGE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.txtSizeHuge.setBackgroundResource(if (size == "HUGE") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun updateLivePreview() {
        val text = binding.etPreviewInput.text?.toString() ?: ""
        if (text.isEmpty()) {
            binding.tvRealtimePreview.text = getString(R.string.type_preview_hint)
            return
        }

        val unicode = viewModel.selectedUnicodeStyle.value
        val shape = viewModel.selectedShapeLayout.value
        val frame = viewModel.selectedFrameStyle.value
        val glitter = viewModel.glitterEnabled.value
        val signature = viewModel.customSignature.value

        var processed = UnicodeStylingEngine.applyStyle(text, unicode)

        if (glitter) {
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

        processed = ShapeEngine.applyShape(processed, shape)
        processed = ArtEngine.applyFrame(processed, frame)
        if (signature.isNotEmpty()) {
            processed = "$processed\n$signature"
        }

        // Apply Rainbow Coloring and Giant Sizing preview style dynamically
        binding.tvRealtimePreview.text = PreviewStyler.stylePreview(
            processed,
            viewModel.colorfulPreviewEnabled.value,
            viewModel.giantWordsEnabled.value
        )
    }
}
