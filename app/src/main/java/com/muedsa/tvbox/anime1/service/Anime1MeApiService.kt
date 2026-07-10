package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.model.AnimeItem
import retrofit2.http.GET
import retrofit2.http.Query

interface Anime1MeApiService {

    @GET("animelist.json")
    suspend fun animeList(
        @Query("_") t: Long = System.currentTimeMillis(),
    ): List<AnimeItem>

}