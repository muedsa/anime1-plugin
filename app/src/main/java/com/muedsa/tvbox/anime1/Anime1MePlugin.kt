package com.muedsa.tvbox.anime1

import com.muedsa.tvbox.anime1.service.Anime1MeAnimeListCacheService
import com.muedsa.tvbox.anime1.service.Anime1MeApiService
import com.muedsa.tvbox.anime1.service.MainScreenService
import com.muedsa.tvbox.anime1.service.MediaCatalogService
import com.muedsa.tvbox.anime1.service.MediaDetailService
import com.muedsa.tvbox.anime1.service.MediaSearchService
import com.muedsa.tvbox.api.plugin.IPlugin
import com.muedsa.tvbox.api.plugin.PluginOptions
import com.muedsa.tvbox.api.plugin.TvBoxContext
import com.muedsa.tvbox.api.service.IMainScreenService
import com.muedsa.tvbox.api.service.IMediaCatalogService
import com.muedsa.tvbox.api.service.IMediaDetailService
import com.muedsa.tvbox.api.service.IMediaSearchService
import com.muedsa.tvbox.api.store.IPluginPerfStore
import com.muedsa.tvbox.tool.IPv6Checker
import com.muedsa.tvbox.tool.PluginCookieJar
import com.muedsa.tvbox.tool.SharedCookieSaver
import com.muedsa.tvbox.tool.createJsonRetrofit
import com.muedsa.tvbox.tool.createOkHttpClient

class Anime1MePlugin(tvBoxContext: TvBoxContext) : IPlugin(tvBoxContext = tvBoxContext) {

    private val store: IPluginPerfStore = tvBoxContext.store

    private val cookieSaver by lazy { SharedCookieSaver(store = store) }

    override var options: PluginOptions = PluginOptions(enableDanDanPlaySearch = true)

    private val cookieJar by lazy { PluginCookieJar(saver = cookieSaver) }
    private val okHttpClient by lazy {
        createOkHttpClient(
            debug = tvBoxContext.debug,
            cookieJar = cookieJar,
            onlyIpv4 = tvBoxContext.iPv6Status != IPv6Checker.IPv6Status.SUPPORTED
        )
    }
    private val anime1MeApiService by lazy {
        createJsonRetrofit(
            baseUrl = "${Anime1MeConst.URL}/",
            service = Anime1MeApiService::class.java,
            okHttpClient = okHttpClient,
        )
    }
    private val anime1MeAnimeListCacheService by lazy {
        Anime1MeAnimeListCacheService(anime1MeApiService = anime1MeApiService)
    }

    private val mainScreenService by lazy {
        MainScreenService(
            anime1MeAnimeListCacheService = anime1MeAnimeListCacheService,
        )
    }
    private val mediaDetailService by lazy {
        MediaDetailService(
            okHttpClient = okHttpClient,
            cookieJar = cookieJar,
        )
    }
    private val mediaSearchService by lazy {
        MediaSearchService(
            anime1MeAnimeListCacheService = anime1MeAnimeListCacheService,
        )
    }
    private val mediaCatalogService by lazy {
        MediaCatalogService(
            anime1MeAnimeListCacheService = anime1MeAnimeListCacheService,
        )
    }

    override fun provideMainScreenService(): IMainScreenService = mainScreenService

    override fun provideMediaDetailService(): IMediaDetailService = mediaDetailService

    override fun provideMediaSearchService(): IMediaSearchService = mediaSearchService

    override fun provideMediaCatalogService(): IMediaCatalogService = mediaCatalogService

    override suspend fun onInit() {}

    override suspend fun onLaunched() {}
}