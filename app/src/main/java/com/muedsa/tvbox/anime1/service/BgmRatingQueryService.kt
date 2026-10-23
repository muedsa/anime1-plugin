package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.model.AnimeItem
import com.muedsa.tvbox.anime1.model.BgmRatingBatchQueryItem
import com.muedsa.tvbox.anime1.model.BgmRatingBatchQueryReq
import com.muedsa.tvbox.anime1.model.BgmRatingBatchQueryResp
import com.muedsa.tvbox.anime1.model.BgmRatingBatchQueryResult
import retrofit2.http.Body
import retrofit2.http.POST

interface BgmRatingQueryService {

    @POST("api/v1/ratings/batch")
    suspend fun batchQuery(@Body req: BgmRatingBatchQueryReq): BgmRatingBatchQueryResp

    companion object {

        const val MAX_BATCH_QUERY_ITEM_NUM = 20

        suspend fun loopBatchQuery(
            animeItems: List<AnimeItem>,
            service: BgmRatingQueryService
        ): Map<String, BgmRatingBatchQueryResult> {
            val chunkedItems = animeItems
                .map {
                    BgmRatingBatchQueryItem(
                        inputId = "${it.cat}",
                        names = buildList {
                            add(it.title)
                            if (it.rawTitle.isNotBlank()) {
                                add(it.rawTitle)
                            }
                        }
                    )
                }.distinctBy { it.inputId }
                .chunked(MAX_BATCH_QUERY_ITEM_NUM)
            return chunkedItems.map { items ->
                try {
                    val resp = service.batchQuery(
                        req = BgmRatingBatchQueryReq(items = items)
                    )
                    return resp.results?.filter {
                        it.status == "matched" && it.rating != null
                    }?.associateBy { it.inputId } ?: emptyMap()
                } catch (_: Throwable) {
                    emptyMap<String, BgmRatingBatchQueryResult>()
                }
            }.flatMap { it.entries }
                .associate { it.key to it.value }
        }
    }
}