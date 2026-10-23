package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class BgmRatingBatchQueryReq(
    val items: List<BgmRatingBatchQueryItem>
)
