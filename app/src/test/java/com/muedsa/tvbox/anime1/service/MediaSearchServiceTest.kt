package com.muedsa.tvbox.anime1.service

import com.muedsa.tvbox.anime1.TestPlugin
import com.muedsa.tvbox.anime1.checkMediaCardRow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.intArrayOf

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MediaSearchServiceTest {

    private val service = TestPlugin.provideMediaSearchService()

    @Test
    fun searchMedias_test() = runTest {
        val row = service.searchMedias("Girls Band Cry")
        checkMediaCardRow(row = row)
    }
}