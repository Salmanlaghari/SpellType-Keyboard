package com.spelltype.keyboard.presentation.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spelltype.keyboard.databinding.ItemArtGalleryStyleBinding
import com.spelltype.keyboard.domain.ArtEngine
import com.spelltype.keyboard.domain.StyleCategorizer
import com.spelltype.keyboard.domain.model.FrameStyle

class GalleryStyleAdapter(
    private val favorites: Set<String>,
    private val onFavoriteClick: (FrameStyle) -> Unit,
    private val onCopyClick: (String) -> Unit,
    private val onShareClick: (String) -> Unit
) : ListAdapter<FrameStyle, GalleryStyleAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArtGalleryStyleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemArtGalleryStyleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(style: FrameStyle) {
            val title = if (style == FrameStyle.NONE) "Normal" else style.name.replace("_", " ")
            binding.tvStyleTitle.text = title

            val isFav = favorites.contains(style.name)
            binding.btnFavorite.text = if (isFav) "♥" else "♡"

            // Set premium lock
            val isPrem = StyleCategorizer.isPremium(style)
            binding.tvPremiumBadge.visibility = if (isPrem) View.VISIBLE else View.GONE

            // Apply frame style preview of "SpellType"
            val previewText = ArtEngine.applyFrame("SpellType", style)
            binding.tvStylePreview.text = previewText

            binding.btnFavorite.setOnClickListener {
                onFavoriteClick(style)
            }

            binding.btnCopy.setOnClickListener {
                onCopyClick(previewText)
            }

            binding.btnShare.setOnClickListener {
                onShareClick(previewText)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FrameStyle>() {
        override fun areItemsTheSame(oldItem: FrameStyle, newItem: FrameStyle): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FrameStyle, newItem: FrameStyle): Boolean {
            return oldItem == newItem
        }
    }
}
