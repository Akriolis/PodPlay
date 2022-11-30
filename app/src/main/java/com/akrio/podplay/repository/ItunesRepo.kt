package com.akrio.podplay.repository

import com.akrio.podplay.service.ItunesService

class ItunesRepo(private val itunesService: ItunesService) {

    suspend fun searchByTerm(term: String) =
        itunesService.searchPodcastByTerm(term)

}