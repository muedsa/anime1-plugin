package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayUrl(
    val src: String = "",
    val type: String = "",
)
