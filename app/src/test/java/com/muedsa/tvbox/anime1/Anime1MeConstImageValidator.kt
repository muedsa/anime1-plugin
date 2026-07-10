package com.muedsa.tvbox.anime1

import com.muedsa.tvbox.tool.checkSuccess
import com.muedsa.tvbox.tool.createOkHttpClient
import com.muedsa.tvbox.tool.get
import com.muedsa.tvbox.tool.toRequestBuild
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class Anime1MeConstImageValidator {

    private val okHttpClient = createOkHttpClient(debug = true)

    @Test
    fun validImages() {
        for (img in Anime1MeConst.IMG_LIST) {
            img.toRequestBuild()
                .get(okHttpClient = okHttpClient)
                .checkSuccess()
        }
    }
}