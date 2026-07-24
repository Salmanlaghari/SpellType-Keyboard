package com.spelltype.keyboard.presentation.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.spelltype.keyboard.R
import com.spelltype.keyboard.databinding.ActivityArtGalleryBinding
import com.spelltype.keyboard.data.db.SpellTypeDatabase
import com.spelltype.keyboard.data.datastore.KeyboardPreferences
import com.spelltype.keyboard.data.repository.KeyboardRepositoryImpl
import com.spelltype.keyboard.domain.StyleCategorizer
import com.spelltype.keyboard.domain.model.FrameStyle
import kotlinx.coroutines.launch

class ArtGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtGalleryBinding
    private lateinit var viewModel: SettingsViewModel
    private var adapter: GalleryStyleAdapter? = null

    private var activeCategory = "All"
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewModel
        val database = SpellTypeDatabase.getDatabase(applicationContext)
        val preferences = KeyboardPreferences(applicationContext)
        val repository = KeyboardRepositoryImpl(database.savedArtDao(), preferences)
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(repository))[SettingsViewModel::class.java]

        setupToolbar()
        setupListeners()
        observeState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.galleryToolbar)
        binding.galleryToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        // Search text watcher
        binding.etGallerySearch.addTextChangedListener { text ->
            searchQuery = text?.toString() ?: ""
            updateList()
        }

        // Category tab chips
        binding.tabAll.setOnClickListener { selectCategory("All") }
        binding.tabClassic.setOnClickListener { selectCategory("Classic") }
        binding.tabSymbol.setOnClickListener { selectCategory("Symbol") }
        binding.tabEmoji.setOnClickListener { selectCategory("Emoji") }
        binding.tabFavorites.setOnClickListener { selectCategory("Favorites") }

        // Unlock Premium button (Mock flow)
        binding.btnUnlockPremium.setOnClickListener {
            viewModel.setPremiumUnlocked(true)
            Toast.makeText(this, "Premium Features Unlocked Successfully! 👑", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectCategory(category: String) {
        activeCategory = category
        updateTabsHighlighting()
        updateList()
    }

    private fun updateTabsHighlighting() {
        binding.tabAll.setBackgroundResource(if (activeCategory == "All") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.tabClassic.setBackgroundResource(if (activeCategory == "Classic") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.tabSymbol.setBackgroundResource(if (activeCategory == "Symbol") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.tabEmoji.setBackgroundResource(if (activeCategory == "Emoji") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
        binding.tabFavorites.setBackgroundResource(if (activeCategory == "Favorites") R.drawable.chip_active_background else R.drawable.chip_inactive_background)
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect Favorites Set
                launch {
                    viewModel.favoriteStyles.collect { favorites ->
                        setupRecyclerView(favorites)
                        updateList()
                    }
                }

                // Collect Premium status
                launch {
                    viewModel.premiumUnlocked.collect { unlocked ->
                        binding.bannerPremium.visibility = if (unlocked) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView(favorites: Set<String>) {
        adapter = GalleryStyleAdapter(
            favorites = favorites,
            onFavoriteClick = { style -> viewModel.toggleFavoriteStyle(style) },
            onCopyClick = { text -> copyToClipboard(text) },
            onShareClick = { text -> shareText(text) }
        )
        binding.rvGalleryStyles.layoutManager = LinearLayoutManager(this)
        binding.rvGalleryStyles.adapter = adapter
    }

    private fun updateList() {
        val allStyles = FrameStyle.values().toList()

        // 1. Filter by category
        var filtered = when (activeCategory) {
            "Classic" -> allStyles.filter { StyleCategorizer.getCategory(it) == "Classic" }
            "Symbol" -> allStyles.filter { StyleCategorizer.getCategory(it) == "Symbol" }
            "Emoji" -> allStyles.filter { StyleCategorizer.getCategory(it) == "Emoji" }
            "Favorites" -> allStyles.filter { viewModel.favoriteStyles.value.contains(it.name) }
            else -> allStyles
        }

        // 2. Filter by search
        if (searchQuery.isNotEmpty()) {
            val query = searchQuery.lowercase()
            filtered = filtered.filter { it.name.lowercase().contains(query) }
        }

        adapter?.submitList(filtered)
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SpellType Art", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard!", Toast.LENGTH_SHORT).show()
    }

    private fun shareText(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Styled Art via")
        startActivity(shareIntent)
    }
}
