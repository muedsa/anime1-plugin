package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.Anime1MeConst
import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardType
import com.muedsa.tvbox.api.data.MediaCatalogConfig
import com.muedsa.tvbox.api.data.MediaCatalogOption
import com.muedsa.tvbox.api.data.PagingResult
import com.muedsa.tvbox.api.service.IMediaCatalogService

class MediaCatalogService(
    private val anime1MeAnimeListCacheService: Anime1MeAnimeListCacheService,
    private val bgmRatingQueryService: BgmRatingQueryService,
) : IMediaCatalogService {

    override suspend fun getConfig(): MediaCatalogConfig {
        return MediaCatalogConfig(
            initKey = "0",
            pageSize = PAGE_SIZE,
            cardWidth = Anime1MeConst.CARD_WIDTH,
            cardHeight = Anime1MeConst.CARD_HEIGHT,
            cardType = MediaCardType.NOT_IMAGE,
            catalogOptions = listOf()
        )
    }

    override suspend fun catalog(
        options: List<MediaCatalogOption>,
        loadKey: String,
        loadSize: Int
    ): PagingResult<MediaCard> {
        val index = loadKey.toInt()
        val animeList = anime1MeAnimeListCacheService.animeList()
        var offset = index + loadSize
        if (offset > animeList.size) {
            offset = animeList.size
        }
        val prevKey = index - PAGE_SIZE
        val subAnimeList = animeList.subList(index, offset)
        val matchedResults = BgmRatingQueryService.loopBatchQuery(
            animeItems = subAnimeList,
            service = bgmRatingQueryService,
        )
        return PagingResult(
            list = subAnimeList.mapIndexed { index, item ->
                val id = item.cat.toString()
                var subTitle = "${item.status} ${item.year} ${item.season}"
                if (matchedResults[id]?.rating?.score != null) {
                    subTitle = "$subTitle ★${matchedResults[id]?.rating?.score}"
                }
                MediaCard(
                    id = id,
                    title = item.title,
                    subTitle = subTitle,
                    detailUrl = id,
                    backgroundColor = Anime1MeConst.getColor(index),
                )
            },
            prevKey = if (prevKey < 0) null else "$prevKey",
            nextKey = if (offset >= animeList.size) null else "$offset"
        )
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}