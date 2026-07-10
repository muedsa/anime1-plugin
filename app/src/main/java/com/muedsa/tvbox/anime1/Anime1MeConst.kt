package com.muedsa.tvbox.anime1

import kotlin.random.Random

object Anime1MeConst {
    const val URL = "https://anime1.me"

    val IMG_LIST = listOf(
        "https://sta.anicdn.com/playerImg/5.jpg",
        "https://sta.anicdn.com/playerImg/6.jpg",
        "https://sta.anicdn.com/playerImg/7.jpg",
        "https://sta.anicdn.com/playerImg/8.jpg",
        "https://sta.anicdn.com/playerImg/9.jpg",
    )

    val COLOR_LIST = listOf(
        0xFF_1A_2A_3A,
        0xFF_2A_1A_3A,
        0xFF_1A_3A_2A,
        0xFF_3A_1A_1A,
        0xFF_3A_2A_1A,
        0xFF_1A_3A_3A,
    )

    fun getImage(row: Int, col: Int, rowNum: Int): String {
        val index = (row * rowNum + col) % IMG_LIST.size
        return IMG_LIST[index]
    }

    fun getRandomImage(): String {
        return IMG_LIST[Random.nextInt(IMG_LIST.size)]
    }

    fun getColor(index: Int): Long {
        return COLOR_LIST[index % COLOR_LIST.size]
    }

    fun getColor(row: Int, col: Int, rowNum: Int): Long {
        val index = (row * rowNum + col) % COLOR_LIST.size
        return COLOR_LIST[index]
    }

    const val CARD_WIDTH = 200
    const val CARD_HEIGHT = 80
}