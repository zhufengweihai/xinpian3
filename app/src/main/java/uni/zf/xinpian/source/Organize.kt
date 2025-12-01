package uni.zf.xinpian.source

object Organize {
    val M_GENRES = listOf(
        "剧情", "喜剧", "爱情", "动作", "惊悚", "犯罪", "恐怖", "悬疑", "冒险", "奇幻", "科幻", "动画",
        "纪录", "战争", "历史", "家庭", "传记", "古装", "音乐", "同性", "情色", "武侠", "运动", "歌舞",
        "短片", "儿童", "西部", "灾难", "黑色", "戏曲", "伦理", "脱口秀"
    )
    val S_GENRES = listOf(
        "剧情",
        "爱情",
        "喜剧",
        "悬疑",
        "犯罪",
        "古装",
        "动作",
        "惊悚",
        "奇幻",
        "家庭",
        "科幻",
        "历史",
        "战争",
        "武侠",
        "恐怖",
        "同性",
        "冒险",
        "纪录",
        "传记",
        "短片",
        "音乐",
        "运动",
        "儿童",
        "生活",
        "都市",
        "西部",
        "歌舞"
    )
    val V_GENRES = listOf(
        "真人秀",
        "纪录片",
        "脱口秀",
        "音乐",
        "歌舞",
        "相声",
        "喜剧",
        "晚会",
        "爱情",
        "历史",
        "运动",
        "冒险",
        "家庭",
        "传记",
        "访谈",
        "儿童",
        "美食",
        "动物",
        "戏曲"
    )
    val A_GENRES = listOf(
        "剧情",
        "喜剧",
        "冒险",
        "奇幻",
        "爱情",
        "儿童",
        "家庭",
        "短片",
        "悬疑",
        "运动",
        "惊悚",
        "恐怖",
        "武侠",
        "古装",
        "犯罪",
        "战争",
        "音乐",
        "搞笑",
        "歌舞",
        "历史",
        "战斗",
        "经典",
        "同性",
        "热血",
        "校园",
        "后宫",
        "少女",
        "情色"
    )
    val AREAS = listOf(
        "中国",
        "中国台湾",
        "中国香港",
        "美国",
        "日本",
        "韩国",
        "英国",
        "法国",
        "印度",
        "加拿大",
        "西班牙",
        "泰国",
        "俄罗斯",
        "德国",
        "菲律宾",
        "意大利",
        "新加坡",
        "马来西亚"
    )
    private val areaMapping = mapOf(
        "大陆" to "中国",
        "中国大陆" to "中国",
        "中国" to "中国",
        "内地" to "中国",
        "香港" to "中国香港",
        "台湾" to "中国台湾"
    )

    private val genreMapping = mapOf(
        "剧情片" to "剧情",
        "喜剧片" to "喜剧",
        "爱情片" to "爱情",
        "动作片" to "动作",
        "恐怖片" to "恐怖",
        "科幻片" to "科幻",
        "动画片" to "动画",
        "纪录片" to "纪录",
        "记录" to "纪录",
        "记录片" to "纪录",
        "紀錄片" to "纪录",
        "战争片" to "战争",
        "黑色电影" to "黑色",
        "理论" to "伦理"
    )

    fun organizeAreas(areas: List<String>): List<String> {
        return organize(areas, areaMapping, AREAS)
    }

    fun organizeGenres(genres: List<String>, standardList: List<String>): List<String> {
        return organize(genres, genreMapping, standardList).let {
            if (it.size > 3 && "剧情" in it) it - "剧情" else it
        }.take(3)
    }

    private fun organize(items: List<String>, mapping: Map<String, String>, validList: List<String>): List<String> {
        return items.map { mapping[it.trim()] ?: it.trim() }
            .map { if (it in validList) it else "其他" }
            .distinct()
            .filterNot { it.isEmpty() }
            .let {
                if (it.contains("其他") && it.size > 1) it - "其他" else it
            }
            .take(3)
    }
}