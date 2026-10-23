package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.Anime1MeConst
import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.data.MediaCardType
import com.muedsa.tvbox.api.service.IMainScreenService

class MainScreenService(
    private val anime1MeAnimeListCacheService: Anime1MeAnimeListCacheService,
    private val bgmRatingQueryService: BgmRatingQueryService,
) : IMainScreenService {

    override suspend fun getRowsData(): List<MediaCardRow> {
        var animeList = anime1MeAnimeListCacheService.animeList()
        if (animeList.size > MAX_NUM) {
            animeList = animeList.subList(0, MAX_NUM)
        }
        val matchedResults = BgmRatingQueryService.loopBatchQuery(
            animeItems = animeList,
            service = bgmRatingQueryService,
        )
        return animeList.chunked(ROW_MAX_NUM).mapIndexed { rowIndex, items ->
            if (rowIndex == 0) {
                MediaCardRow(
                    title = "動畫列表 1",
                    cardWidth = 192,
                    cardHeight = 108,
                    cardType = MediaCardType.STANDARD,
                    list = items.mapIndexed { colIndex, item ->
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
                            coverImageUrl = Anime1MeConst.getImage(rowIndex, colIndex, ROW_MAX_NUM)
                        )
                    }
                )
            } else {
                MediaCardRow(
                    title = "動畫列表 ${rowIndex + 1}",
                    cardWidth = Anime1MeConst.CARD_WIDTH,
                    cardHeight = Anime1MeConst.CARD_HEIGHT,
                    cardType = MediaCardType.NOT_IMAGE,
                    list = items.mapIndexed { colIndex, item ->
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
                            backgroundColor = Anime1MeConst.getColor(
                                rowIndex,
                                colIndex,
                                ROW_MAX_NUM
                            )
                        )
                    }
                )
            }
        }
    }

    companion object {
        const val ROW_MAX_NUM = 20
        const val MAX_NUM = ROW_MAX_NUM * 5
    }
}