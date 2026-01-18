package uni.zf.xinpian.data

import uni.zf.xinpian.utils.generateJpUrlPrefix

object AppConst {
    const val KEY_SECRET = "secret"
    const val DEFAULT_SECRET = "0sD4gjkMdbnsYp5k4K0oB5MGMggyp9UP"
    const val USER_AGENT_SUFFIX = ";webank/h5face;webank/1.0;netType:%s;appVersion:424;packageName:com.qihoo.jp22"
    const val NETWORK_WIFI = "NETWORK_WIFI"
    const val NETWORK_MOBILE = "NETWORK_MOBILE"
    const val KEY_IMG_DOMAINS = "imgDomains"
    const val DEFAULT_IMG_DOMAINS =
        "static.ztcuc.com,img4.ztcuc.com,img.ztcuc.com,oxcljymt.top,lnijywwg.top,pxluvojc.top"
    const val VERSION = "417"
    const val PACKAGE_NAME = "com.qihoo.jp22"
    const val ARG_CATEGORY = "category"
    const val KEY_VIDEO_ID = "video_id"
    private val jpUrlPrefix = generateJpUrlPrefix()
    val host = "${jpUrlPrefix}.zxbwv.com"
    val imgDomainUrl = "https://${jpUrlPrefix}.zxbwv.com/api/resourceDomainConfig"
    val categoryUrl = "https://${jpUrlPrefix}.zxbwv.com/api/term/home_fenlei"
    val initUrl = "https://${jpUrlPrefix}.zxbwv.com/api/v2/sys/init"
    val slideUrl = "https://${jpUrlPrefix}.zxbwv.com/api/slide/list?pos_id=%s"
    val tagsUrl = "https://${jpUrlPrefix}.zxbwv.com/api/customTags/list?category_id=%s"
    val dyTagURL = "https://${jpUrlPrefix}.zxbwv.com/api/dyTag/list?category_id=%s"
    val appAuthUrl = "https://${jpUrlPrefix}.zxbwv.com/api/appAuthConfig"
    val videoUrl = "https://${jpUrlPrefix}.zxbwv.com/api/video/detailv2?id=%d"
    val recoUrl = "https://${jpUrlPrefix}.zxbwv.com/api/video/guessRecommend?id=%s&page=1"
    val shortUrl = "https://${jpUrlPrefix}.zxfmj.com/api/play?count=10&type=1&page=%d&order=2&token=&init=0"
    val discoverUrl = "https://${jpUrlPrefix}.zxfmj.com/api/discover/list?type=1&page=%d&pageSize=20"
    val specialUrl = "https://${jpUrlPrefix}.zxfmj.com/api/special/list?page=%d"
    val rankOptionsUrl = "https://yebcdc.zxfmj.com/api/weekRank/options"
}

