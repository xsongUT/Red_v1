package com.example.red_v1

// This is the model in MVVM
data class SongInfo(val name: String, val rawId: Int, val time: String,
    val uniqueId: Int)
class Repository {
    private var songResources = listOf(
                SongInfo(
                    "Attention",
                    R.raw.attention,
                    "3:31",
                    0
                ),
                SongInfo(
                    "Summer Wine",
                    R.raw.summer_wine,
                    "4:55",
                    1
                ),
                SongInfo(
                    "Apologize",
                    R.raw.apologize,
                    "3:27",
                    2
                ),
                SongInfo(
                    "There’s Nothing Holdin’ Me Back",
                    R.raw.theres_nothing_holdin_me,
                    "3:19",
                    3
                ),
                SongInfo(
                    "Young And Beautiful",
                    R.raw.young_and_beautiful,
                    "3:56",
                    4
                ),
                SongInfo(
                    "Rolling In the Deep",
                    R.raw.rolling_in_the_deep,
                    "3:48",
                    5
                ),
                SongInfo(
                    "Galway Girl",
                    R.raw.galway_girl,
                    "2:50",
                    6
                ),
                SongInfo(
                    "Stitches",
                    R.raw.stitches,
                    "3:26",
                    7
                ),
                SongInfo(
                    "Faded",
                    R.raw.faded,
                    "3:32",
                    8
                ),
                SongInfo(
                    "Dance Monkey",
                    R.raw.dance_monkey,
                    "3:29",
                    9
                ),
                SongInfo(
                    "City of Stars",
                    R.raw.city_of_stars,
                    "2:34",
                    10
                ),
                SongInfo(
                    "Animals",
                    R.raw.animals,
                    "3:13",
                    11
                ),
                SongInfo(
                    "Poker Face",
                    R.raw.poker_face,
                    "3:57",
                    12
                ),
                SongInfo(
                    "Love Story",
                    R.raw.love_story,
                    "3:56",
                    13
                ),
        SongInfo(
            "Attention",
            R.raw.attention,
            "3:31",
            14
        ),
        SongInfo(
            "Summer Wine",
            R.raw.summer_wine,
            "4:55",
            15
        ),
        SongInfo(
            "Apologize",
            R.raw.apologize,
            "3:27",
            16
        ),
        SongInfo(
            "There’s Nothing Holdin’ Me Back",
            R.raw.theres_nothing_holdin_me,
            "3:19",
            17
        ),
        SongInfo(
            "Young And Beautiful",
            R.raw.young_and_beautiful,
            "3:56",
            18
        ),
        SongInfo(
            "Rolling In the Deep",
            R.raw.rolling_in_the_deep,
            "3:48",
            19
        ),
        SongInfo(
            "Galway Girl",
            R.raw.galway_girl,
            "2:50",
            20
        ),
        SongInfo(
            "Stitches",
            R.raw.stitches,
            "3:26",
            21
        ),
        SongInfo(
            "Faded",
            R.raw.faded,
            "3:32",
            22
        ),
        SongInfo(
            "Dance Monkey",
            R.raw.dance_monkey,
            "3:29",
            23
        ),
        SongInfo(
            "City of Stars",
            R.raw.city_of_stars,
            "2:34",
            24
        ),
        SongInfo(
            "Animals",
            R.raw.animals,
            "3:13",
            25
        ),
        SongInfo(
            "Poker Face",
            R.raw.poker_face,
            "3:57",
            26
        ),
        SongInfo(
            "Love Story",
            R.raw.love_story,
            "3:56",
            27
        )
    )
    //EEE
    fun fetchData(): List<SongInfo> {
        return songResources
    }
}