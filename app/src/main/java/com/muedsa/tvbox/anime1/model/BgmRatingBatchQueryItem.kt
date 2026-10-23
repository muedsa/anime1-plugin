package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class BgmRatingBatchQueryItem(
    val inputId: String,
    val names: List<String>,
)
