package com.akrio.podplay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akrio.podplay.databinding.EpisodeItemBinding
import com.akrio.podplay.util.DateUtils
import com.akrio.podplay.util.HtmlUtils
import com.akrio.podplay.viewmodel.PodcastViewModel

class EpisodeListAdapter(
    private var episodeViewList: List<PodcastViewModel.EpisodeViewData>?
) : RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>() {

    inner class ViewHolder(
        binding: EpisodeItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var episodeViewData: PodcastViewModel.EpisodeViewData? = null
        val titleTextView: TextView = binding.titleView
        val descTextView: TextView = binding.descView
        val durationTextView: TextView = binding.durationView
        val releaseDateTextView: TextView = binding.releaseDateView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            EpisodeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeViewList = episodeViewList ?: return
        val episodeView = episodeViewList[position]

        holder.episodeViewData = episodeView
        holder.titleTextView.text = episodeView.title
        holder.descTextView.text = HtmlUtils.htmlToSpannable(episodeView.description ?: "")
        holder.durationTextView.text = episodeView.duration
        holder.releaseDateTextView.text = episodeView.releaseDate?.let {
            DateUtils.dateToShortDate(it)
        }
    }

    override fun getItemCount(): Int = episodeViewList?.size ?: 0
}