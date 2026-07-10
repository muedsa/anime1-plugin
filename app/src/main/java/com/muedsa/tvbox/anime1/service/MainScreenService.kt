package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.Anime1MeConst
import com.muedsa.tvbox.api.data.MediaCard
import com.muedsa.tvbox.api.data.MediaCardRow
import com.muedsa.tvbox.api.data.MediaCardType
import com.muedsa.tvbox.api.service.IMainScreenService

class MainScreenService(
    private val anime1MeAnimeListCacheService: Anime1MeAnimeListCacheService,
) : IMainScreenService {

    override suspend fun getRowsData(): List<MediaCardRow> {
        var animeList = anime1MeAnimeListCacheService.animeList()
        if (animeList.size > MAX_NUM) {
            animeList = animeList.subList(0, MAX_NUM)
        }
        return animeList.chunked(ROW_MAX_NUM).mapIndexed { rowIndex, items ->
            if (rowIndex == 0) {
                MediaCardRow(
                    title = "動畫列表 1",
                    cardWidth = 192,
                    cardHeight = 108,
                    cardType = MediaCardType.STANDARD,
                    list = items.mapIndexed { colIndex, item ->
                        MediaCard(
                            id = item.cat.toString(),
                            title = item.title,
                            subTitle = "${item.status} ${item.year} ${item.season}",
                            detailUrl = item.cat.toString(),
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
                        MediaCard(
                            id = item.cat.toString(),
                            title = item.title,
                            subTitle = "${item.status} ${item.year} ${item.season}",
                            detailUrl = item.cat.toString(),
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