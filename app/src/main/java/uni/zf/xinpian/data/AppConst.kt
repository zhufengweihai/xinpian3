package uni.zf.xinpian.data

import uni.zf.xinpian.http.DomainManager
import uni.zf.xinpian.utils.generateJpUrlPrefix

object AppConst {
    const val DOMAIN_URL = "https://jdomain.oss-accelerate.aliyuncs.com/domain.txt"
    const val TOKEN = "1b80ef50a3cd601776bb98d950f74f85"
    const val VPN_URL = "https://pandavpnpro.com/r/110177713"
    const val GYLM_URL = "https://review-2026.kuaizhan.com/?code_sign=gyic_38039011&_d=20260506"
    const val KEY_SECRET = "secret"
    const val DEFAULT_SECRET = "0sD4gjkMdbnsYp5k4K0oB5MGMggyp9UP"
    const val USER_AGENT_SUFFIX = ";webank/h5face;webank/1.0;netType:%s;appVersion:424;packageName:com.qihoo.jp22"
    const val NETWORK_WIFI = "NETWORK_WIFI"
    const val NETWORK_MOBILE = "NETWORK_MOBILE"
    const val KEY_IMG_DOMAINS = "imgDomains"
    const val DEFAULT_IMG_DOMAINS =
        "static.ztcuc.com,img4.ztcuc.com,img.ztcuc.com,oxcljymt.top,lnijywwg.top,pxluvojc.top"
    const val VERSION = "500"
    const val DEFAULT_CATEGORY_ID = "88"
    const val PACKAGE_NAME = "com.sam.mobile"
    const val ARG_CATEGORY = "category"
    const val ARG_DY_TAG = "DyTag"
    const val ARG_TAG_TITLE = "tagTitle"
    const val ARG_TAG_URL = "customTagDataUrl"
    const val ARG_FILTER_OPTIONS = "filterOptions"
    const val ARG_VIDEO_ID = "video_id"
    const val ARG_SHORT_ID = "vid"
    const val ARG_KEYWORD = "keyword"
    const val CUSTOM_TAG_START = "/videos"
    const val KEY_VERSION = "version"
    const val DEFAULT_VERSION = "V0.0.0"
    private val jpUrlPrefix = generateJpUrlPrefix()

    /** 动态域名：通过 DomainManager 获取当前活跃域名 */
    val host: String get() = "${jpUrlPrefix}.${DomainManager.currentDomain}"
    val baseUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}"
    val imgDomainUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/resourceDomainConfig"
    val categoryUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/term/home_fenlei"
    val initUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/v2/sys/init"
    val slideUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/slide/list?pos_id=%s"
    val tagsUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/customTags/list?category_id=%s"
    val dyTagURL: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/dyTag/list?category_id=%s"
    val tagVideoListUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/dyTag/tpl2_data?id=%d&page=%%d"
    val appAuthUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/appAuthConfig"
    val videoUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/video/detailv2?id=%d"
    val recoUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/video/guessRecommend?id=%s&page=1"
    val shortListUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/play?count=10&type=1&page=%d&order=2&token=&init=0"
    val shortUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/detail?token=${TOKEN}&vid=%d"
    val discoverUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/discover/list?type=1&page=%d&pageSize=20"
    val specialUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/special/list?page=%d"
    val specialDetailUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/special/detail?id=%d"
    val rankOptionsUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/weekRank/options"
    val weekRankUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/weekRank/list?category_id=%d"
    val filterOptionsUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/crumb/filterOptions"
    val filteredVideoUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}" +
            "/api/crumb/list?fcate_pid=%s&category_id=%s&area=%s&year=%s&type=%s&sort=%s&page=%%d"
    val recommendUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/search/recommend"
    val searchCategoryUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/v2/settings/topCategory"
    val categoryVideoUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/search/searchList?category_id=%d&page=%%d"
    val searchUrl: String get() = "https://${jpUrlPrefix}.${DomainManager.currentDomain}/api/v2/search/videoV2?key=%s&category_id=%d&page=%%d&pageSize=20"
    val appReleaseUrl = "https://api.github.com/repos/zhufengweihai/xinpian3/releases/latest"
}
