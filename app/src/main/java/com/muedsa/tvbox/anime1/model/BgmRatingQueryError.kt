package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class BgmRatingQueryError(
    val code: String = "",
    val message: String = "",
)
