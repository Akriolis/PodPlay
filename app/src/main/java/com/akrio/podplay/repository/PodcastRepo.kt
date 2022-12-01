package com.akrio.podplay.repository

import com.akrio.podplay.model.Podcast

class PodcastRepo {
    fun getPodcast(feedUrl: String): Podcast? {
        return Podcast(feedUrl, "No name", "No description", "No image")
    }
}