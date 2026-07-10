package com.muedsa.tvbox.anime1.service

import com.github.houbb.opencc4j.util.ZhTwConverterUtil
import com.muedsa.tvbox.anime1.Anime1MeConst
import com.muedsa.tvbox.anime1.model.PlayUrls
import com.muedsa.tvbox.api.data.DanmakuData
import com.muedsa.tvbox.api.data.DanmakuDataFlow
import com.muedsa.tvbox.api.data.MediaDetail
import com.muedsa.tvbox.api.data.MediaEpisode
import com.muedsa.tvbox.api.data.MediaHttpSource
import com.muedsa.tvbox.api.data.MediaPlaySource
import com.muedsa.tvbox.api.data.SavedMediaCard
import com.muedsa.tvbox.api.service.IMediaDetailService
import com.muedsa.tvbox.tool.ChromeUserAgent
import com.muedsa.tvbox.tool.LenientJson
import com.muedsa.tvbox.tool.PluginCookieJar
import com.muedsa.tvbox.tool.checkSuccess
import com.muedsa.tvbox.tool.get
import com.muedsa.tvbox.tool.parseHtml
import com.muedsa.tvbox.tool.post
import com.muedsa.tvbox.tool.stringBody
import com.muedsa.tvbox.tool.toRequestBuild
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.nodes.Element
import timber.log.Timber

class MediaDetailService(
    private val okHttpClient: OkHttpClient,
    private val cookieJar: PluginCookieJar,
) : IMediaDetailService {

    override suspend fun getDetailData(mediaId: String, detailUrl: String): MediaDetail {
        val body =
            "${Anime1MeConst.URL}/?cat=$mediaId".toRequestBuild().get(okHttpClient).checkSuccess()
                .parseHtml().body()
        val title = body.selectFirst("#content .page-header .page-title")!!.text().trim()
        val simpleTitle = ZhTwConverterUtil.toSimple(title)
        val episodes = mutableListOf<MediaEpisode>()
        episodes.addAll(parseEpisodes(body, title))
        body.selectFirst("#content #main .pagination .navigation .nav-links .nav-previous a")?.let {
            val previousUrl = it.attr("href")
            if (previousUrl.isNotBlank()) {
                loopGetEpisodes(previousUrl, title, episodes)
            }
        }
        return MediaDetail(
            id = mediaId,
            title = simpleTitle,
            subTitle = "",
            description = "",
            detailUrl = detailUrl,
            backgroundImageUrl = Anime1MeConst.getRandomImage(),
            playSourceList = listOf(
                MediaPlaySource(
                    id = "anime1.me",
                    name = "anime1.me",
                    episodeList = episodes.reversed(),
                )
            ),
            favoritedMediaCard = SavedMediaCard(
                id = mediaId,
                title = simpleTitle,
                subTitle = "",
                detailUrl = detailUrl,
                coverImageUrl = Anime1MeConst.getRandomImage(),
                cardWidth = Anime1MeConst.CARD_WIDTH,
                cardHeight = Anime1MeConst.CARD_HEIGHT,
            )
        )
    }

    private fun parseEpisodes(body: Element, animeTitle: String): List<MediaEpisode> {
        return body.select("#content #main article").map { articleEl ->
            val title = articleEl.selectFirst(".entry-header .entry-title")!!.text().trim()
                .removePrefix(animeTitle).trim()
            val id = articleEl.id().trim().removePrefix("post-")
            MediaEpisode(
                id = id,
                name = title,
                flag1 = id.toInt(),
                flag5 = articleEl.selectFirst(".entry-header .entry-title a")!!.attr("href")
            )
        }
    }

    private fun loopGetEpisodes(url: String, animeTitle: String, list: MutableList<MediaEpisode>) {
        val body = url.toRequestBuild().get(okHttpClient).checkSuccess().parseHtml().body()
        val episodes = parseEpisodes(body, animeTitle)
        list.addAll(episodes)
        body.selectFirst("#content #main .pagination .navigation .nav-links .nav-previous a")?.let {
            val previousUrl = it.attr("href")
            if (previousUrl.isNotBlank()) {
                loopGetEpisodes(previousUrl, animeTitle, list)
            }
        }
    }

    override suspend fun getEpisodePlayInfo(
        playSource: MediaPlaySource,
        episode: MediaEpisode
    ): MediaHttpSource {
        val url = episode.flag5 ?: "${Anime1MeConst.URL}/${episode.id}"
        val body = url.toRequestBuild().get(okHttpClient).checkSuccess().parseHtml().body()
        val videoEl = body.selectFirst("#content #main article video")
            ?: throw RuntimeException("解析视频播放地址失败, not select video element")
        val d = videoEl.attr("data-apireq")
        if (d.isBlank()) {
            throw RuntimeException("解析视频播放地址失败, d is empty")
        }
        val respBody = "https://v.anime1.me/api".toRequestBuild().post(
            body = "d=${d}".toRequestBody("application/x-www-form-urlencoded".toMediaType()),
            okHttpClient = okHttpClient,
        ).checkSuccess().stringBody()
        val playUrls = LenientJson.decodeFromString<PlayUrls>(respBody)
        if (playUrls.s.isNullOrEmpty()) {
            throw RuntimeException("解析视频播放地址失败, play url")
        }
        var playUrl = playUrls.s[0].src
        if (playUrl.startsWith("//")) {
            playUrl = "https://${playUrl.removePrefix("//")}"
        }
        Timber.d("")
        val cookie = cookieJar.loadForRequest(playUrl.toHttpUrl())
            .joinToString("; ") { "${it.name}=${it.value}" }
        Timber.i("cookie: $cookie")
        return MediaHttpSource(
            url = playUrl,
            httpHeaders = mapOf(
                "Referer" to "${Anime1MeConst.URL}/",
                "Cookie" to cookie,
                "User-Agent" to ChromeUserAgent,
            ),
        )
    }

    override suspend fun getEpisodeDanmakuDataList(episode: MediaEpisode): List<DanmakuData>
        = emptyList()

    override suspend fun getEpisodeDanmakuDataFlow(episode: MediaEpisode): DanmakuDataFlow? = null
}