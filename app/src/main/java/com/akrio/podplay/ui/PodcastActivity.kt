package com.akrio.podplay.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.akrio.podplay.R
import com.akrio.podplay.adapter.PodcastListAdapter
import com.akrio.podplay.databinding.ActivityPodcastBinding
import com.akrio.podplay.repository.ItunesRepo
import com.akrio.podplay.repository.PodcastRepo
import com.akrio.podplay.service.ItunesService
import com.akrio.podplay.viewmodel.PodcastViewModel
import com.akrio.podplay.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PodcastActivity : AppCompatActivity() {

    companion object {
        private const val TAG_DETAILS_FRAGMENT = "DetailsFragment"
    }

    private lateinit var searchMenuItem: MenuItem

    private val searchViewModel by viewModels<SearchViewModel>()
    private val podcastViewModel by viewModels<PodcastViewModel>()

    private lateinit var podcastListAdapter: PodcastListAdapter

    private lateinit var binding: ActivityPodcastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPodcastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupViewModels()
        updateControls()
        handleIntent(intent)
        addBackStackListener()

        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu)
                searchMenuItem = menu.findItem(R.id.search_item)
                val searchView = searchMenuItem.actionView as SearchView
                val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

                searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

                if (supportFragmentManager.backStackEntryCount > 0) {
                    binding.podcastRecyclerView.visibility = View.INVISIBLE
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search_item -> true
                    else -> false
                }
            }

        })
    }

    private fun onShowDetails(podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) {
        val feedUrl = podcastSummaryViewData.feedUrl ?: return
        showProgressBar()
        val podcast = podcastViewModel.getPodcast(podcastSummaryViewData)
        hideProgressBar()
        if (podcast != null) {
            showDetailsFragment()
        } else {
            showError("Error loading feed $feedUrl")
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = menuInflater
//        inflater.inflate(R.menu.menu_search, menu)
//        val searchMenuItem = menu?.findItem(R.id.search_item)
//        val searchView = searchMenuItem?.actionView as SearchView
//        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
//
//        return true
//    }

    private fun performSearch(term: String) {
        showProgressBar()
        lifecycleScope.launch {
            val result = searchViewModel.searchPodcasts(term)
            withContext(Dispatchers.Main) {
                hideProgressBar()
                binding.toolbar.title = term
                podcastListAdapter.setSearchData(result)
            }
        }
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: return
            performSearch(query)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupViewModels() {
        val service = ItunesService.instance
        searchViewModel.iTunesRepo = ItunesRepo(service)
        podcastViewModel.podcastRepo = PodcastRepo()
    }

    private fun updateControls() {
        binding.podcastRecyclerView.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        binding.podcastRecyclerView.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            binding.podcastRecyclerView.context,
            layoutManager.orientation
        )

        binding.podcastRecyclerView.addItemDecoration(dividerItemDecoration)

        val lambdaClicker: (podcastSummaryViewData: SearchViewModel.PodcastSummaryViewData) -> Unit =
            { onShowDetails(it) }

        podcastListAdapter = PodcastListAdapter(null, lambdaClicker, this)
        binding.podcastRecyclerView.adapter = podcastListAdapter
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun createPodcastDetailsFragment(): PodcastDetailsFragment {
        var podcastDetailsFragment = supportFragmentManager
            .findFragmentByTag(TAG_DETAILS_FRAGMENT) as PodcastDetailsFragment?

        if (podcastDetailsFragment == null) {
            podcastDetailsFragment = PodcastDetailsFragment.newInstance()
        }

        return podcastDetailsFragment
    }

    private fun showDetailsFragment() {
        val podcastDetailsFragment = createPodcastDetailsFragment()
        supportFragmentManager.beginTransaction().add(
            R.id.podcastDetailsContainer,
            podcastDetailsFragment,
            TAG_DETAILS_FRAGMENT
        )
            .addToBackStack("DetailsFragment").commit()
        binding.podcastRecyclerView.visibility = View.INVISIBLE
        searchMenuItem.isVisible = false
    }

    private fun showError(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok_button), null)
            .create()
            .show()
    }

    private fun addBackStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                binding.podcastRecyclerView.visibility = View.VISIBLE
            }
        }
    }
}