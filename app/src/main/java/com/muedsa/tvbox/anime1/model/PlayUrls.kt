package com.muedsa.tvbox.anime1.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayUrls(
    val s: List<PlayUrl>? = null
)