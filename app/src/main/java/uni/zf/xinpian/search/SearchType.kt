package uni.zf.xinpian.search

val M_GENRES: Array<String> = arrayOf(
    "全部类型",
    "剧情",
    "喜剧",
    "爱情",
    "动作",
    "惊悚",
    "犯罪",
    "恐怖",
    "悬疑",
    "冒险",
    "奇幻",
    "科幻",
    "动画",
    "纪录",
    "战争",
    "历史",
    "家庭",
    "传记",
    "古装",
    "音乐",
    "同性",
    "情色",
    "武侠",
    "运动",
    "歌舞",
    "短片",
    "儿童",
    "西部",
    "灾难",
    "黑色",
    "戏曲",
    "脱口秀",
    "其他"
)

val S_GENRES: Array<String> = arrayOf(
    "全部类型",
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
    "歌舞",
    "其他"
)

val V_GENRES: Array<String> = arrayOf(
    "全部类型", "真人秀", "纪录片", "脱口秀", "音乐", "歌舞", "相声", "喜剧", "晚会", "爱情", "历史", "运动",
    "冒险", "家庭", "传记", "访谈", "儿童", "美食", "动物", "戏曲", "其他"
)

val A_GENRES: Array<String> = arrayOf(
    "全部类型",
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
    "其他"
)

val D_GENRES: Array<String> = arrayOf(
    "全部类型", "重生民国", "穿越年代", "现代言情", "反转爽文", "女恋总裁", "闪婚离婚", "都市脑洞", "古装仙侠",
    "其他"
)

val ORDER_BY: Array<String> = arrayOf("hotness desc", "vod_time desc", "db_score desc")

enum class SearchType(var values: Array<String>) {
    CATEGORIES(arrayOf("电影", "剧集", "动漫", "综艺", "短剧")), AREAS(
        arrayOf(
            "全部地区",
            "中国",
            "中国台湾",
            "中国香港",
            "美国",
            "日本",
            "韩国",
            "英国",
            "法国",
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
            "马来西亚",
            "其他"
        )
    ),
    YEARS(
        arrayOf(
            "全部年份", "2025", "2024", "2023", "2022", "2021", "2020", "2019", "2010", "2000", "90年代", "80年代",
            "70年代", "60年代", "更早"
        )
    ),
    GENRES(M_GENRES) {
        override fun values(category: String?): Array<String> {
            return when (category) {
                "S" -> S_GENRES
                "A" -> A_GENRES
                "V" -> V_GENRES
                "D" -> D_GENRES
                else -> M_GENRES
            }
        }
    },
    SORTS(
        arrayOf(
            "热度", "更新", "评分"
        )
    );

    open fun values(category: String?): Array<String> {
        return values
    }
}