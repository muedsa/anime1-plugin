package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.model.AnimeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class Anime1MeAnimeListCacheService(
    private val anime1MeApiService: Anime1MeApiService,
) {
    private var animeList: List<AnimeItem>? = null

    private val mutex = Mutex()

    suspend fun animeList(focus: Boolean = false): List<AnimeItem> = mutex.withLock {
        if (focus || animeList == null) {
            animeList = withContext(Dispatchers.IO) {
                anime1MeApiService.animeList()
            }
        }
        return@withLock animeList ?: throw RuntimeException("获取动漫列表失败")
    }
}