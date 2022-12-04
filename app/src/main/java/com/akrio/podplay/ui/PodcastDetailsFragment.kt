package com.akrio.podplay.ui

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.akrio.podplay.R
import com.akrio.podplay.adapter.EpisodeListAdapter
import com.akrio.podplay.databinding.FragmentPodcastDetailsBinding
import com.akrio.podplay.viewmodel.PodcastViewModel
import com.bumptech.glide.Glide

class PodcastDetailsFragment : Fragment() {


    interface OnPodcastDetailsListener {
        fun onSubscribe()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPodcastDetailsListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + "must implement OnPodcastDetailsListener")
        }
    }

    private val podcastViewModel: PodcastViewModel by activityViewModels()

    private var listener: OnPodcastDetailsListener? = null

    private lateinit var binding: FragmentPodcastDetailsBinding

    private lateinit var episodeListAdapter: EpisodeListAdapter

    companion object {
        fun newInstance(): PodcastDetailsFragment {
            return PodcastDetailsFragment()
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPodcastDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_details, menu)
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
//        updateControls()

        podcastViewModel.podcastLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.feedTitleTextView.text = it.feedTitle
                binding.feedDescTextView.text = it.feedDesc
                activity?.let { activity ->
                    Glide.with(activity)
                        .load(it.imageUrl)
                        .into(binding.feedImageView)
                }

                binding.feedDescTextView.movementMethod = ScrollingMovementMethod()
                binding.episodeRecyclerView.setHasFixedSize(true)

                val layoutManager = LinearLayoutManager(activity)
                binding.episodeRecyclerView.layoutManager = layoutManager

                val dividerItemDecoration =
                    DividerItemDecoration(
                        binding.episodeRecyclerView.context,
                        layoutManager.orientation
                    )

                binding.episodeRecyclerView.addItemDecoration(dividerItemDecoration)

                episodeListAdapter = EpisodeListAdapter(it.episodes)
                binding.episodeRecyclerView.adapter = episodeListAdapter
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_details, menu)
                val searchMenuItem = menu.findItem(R.id.search_item)
                searchMenuItem.isVisible = false

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search_item -> true
                    R.id.menu_feed_action -> {
                        podcastViewModel.podcastLiveData.value?.feedUrl?.let {
                            listener?.onSubscribe()
                        }
                        true
                    }
                    else -> false
                }

            }
        }, viewLifecycleOwner)
    }

//    private fun updateControls(){
//        val viewData = podcastViewModel.activePodcastViewData ?: return
//        binding.feedTitleTextView.text = viewData.feedTitle
//        binding.feedDescTextView.text = viewData.feedDesc
//        activity?.let {
//            Glide.with(it)
//                .load(viewData.imageUrl)
//                .into(binding.feedImageView)
//        }
//    }
}