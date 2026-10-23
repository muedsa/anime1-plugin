package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class BgmRatingBatchQuerySubject(
    val id: Long = 0,
    val name: String = "",
    val nameCn: String? = null,
    val airDate: String? = null,
    val platform: String? = null,
    val url: String? = null,
)
