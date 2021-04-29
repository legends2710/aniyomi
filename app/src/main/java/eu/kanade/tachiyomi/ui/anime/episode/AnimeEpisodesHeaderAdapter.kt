package eu.kanade.tachiyomi.ui.anime.episode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.databinding.AnimeEpisodesHeaderBinding
import eu.kanade.tachiyomi.ui.anime.AnimeController
import eu.kanade.tachiyomi.util.system.getResourceColor
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class AnimeEpisodesHeaderAdapter(
    private val controller: AnimeController
) :
    RecyclerView.Adapter<AnimeEpisodesHeaderAdapter.HeaderViewHolder>() {

    private var numEpisodes: Int? = null
    private var hasActiveFilters: Boolean = false

    private lateinit var binding: AnimeEpisodesHeaderBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        binding = AnimeEpisodesHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HeaderViewHolder(binding.root)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind()
    }

    fun setNumEpisodes(numEpisodes: Int) {
        this.numEpisodes = numEpisodes

        notifyDataSetChanged()
    }

    fun setHasActiveFilters(hasActiveFilters: Boolean) {
        this.hasActiveFilters = hasActiveFilters

        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            binding.episodesLabel.text = if (numEpisodes == null) {
                view.context.getString(R.string.chapters)
            } else {
                view.context.resources.getQuantityString(R.plurals.manga_num_chapters, numEpisodes!!, numEpisodes)
            }

            val filterColor = if (hasActiveFilters) {
                view.context.getResourceColor(R.attr.colorFilterActive)
            } else {
                view.context.getResourceColor(R.attr.colorOnBackground)
            }
            DrawableCompat.setTint(binding.btnEpisodesFilter.drawable, filterColor)

            merge(view.clicks(), binding.btnEpisodesFilter.clicks())
                .onEach { controller.showSettingsSheet() }
                .launchIn(controller.viewScope)
        }
    }
}