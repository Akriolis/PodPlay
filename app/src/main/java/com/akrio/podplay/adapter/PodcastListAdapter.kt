package com.akrio.podplay.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akrio.podplay.databinding.SearchItemBinding
import com.akrio.podplay.viewmodel.SearchViewModel
import com.bumptech.glide.Glide

class PodcastListAdapter(
    private var podcastSummaryViewList: List<SearchViewModel.PodcastSummaryViewData>?,
    private val podcastListAdapterListener: (podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) -> Unit,
    private val parentActivity: Activity
) : RecyclerView.Adapter<PodcastListAdapter.ViewHolder>() {

    inner class ViewHolder(
        binding: SearchItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        var podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData? = null
        val nameTextView = binding.podcastNameTextView
        val lastUpdatedTextView = binding.podcastLastUpdatedTextView
        val podcastImageView = binding.podcastImage

        init {
            binding.searchItem.setOnClickListener {
                podcastSummaryViewData?.let {
                    podcastListAdapterListener(it)
                }
            }
        }
    }

    fun setSearchData(podcastSummaryViewData: List<SearchViewModel.PodcastSummaryViewData>) {
        podcastSummaryViewList = podcastSummaryViewData
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PodcastListAdapter.ViewHolder {
        return ViewHolder(
            SearchItemBinding.inflate
                (LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchViewList = podcastSummaryViewList ?: return
        val searchView = searchViewList[position]
        holder.podcastSummaryViewData = searchView
        holder.nameTextView.text = searchView.name
        holder.lastUpdatedTextView.text = searchView.lastUpdated
        Glide.with(parentActivity)
            .load(searchView.imageUrl)
            .into(holder.podcastImageView)
    }

    override fun getItemCount(): Int {
        return podcastSummaryViewList?.size ?: 0
    }


}