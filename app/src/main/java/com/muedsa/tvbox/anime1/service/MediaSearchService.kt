package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.Anime1MeConst
import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.data.MediaCardType
import com.muedsa.tvbox.api.service.IMediaSearchService

class MediaSearchService(
    private val anime1MeAnimeListCacheService: Anime1MeAnimeListCacheService,
) : IMediaSearchService {
    override suspend fun searchMedias(query: String): MediaCardRow {
        val animeList = anime1MeAnimeListCacheService.animeList()
        val filtered = animeList
            .asSequence()
            .filter { it.title.contains(query) }
            .take(20)
            .toList()
        return MediaCardRow(
            title = "search list",
            cardWidth = Anime1MeConst.CARD_WIDTH,
            cardHeight = Anime1MeConst.CARD_HEIGHT,
            cardType = MediaCardType.NOT_IMAGE,
            list = filtered.mapIndexed { index, item ->
                MediaCard(
                    id = item.cat.toString(),
                    title = item.title,
                    subTitle = "${item.status} ${item.year} ${item.season}",
                    detailUrl = item.cat.toString(),
                    backgroundColor = Anime1MeConst.getColor(index),
                )
            }
        )
    }
}