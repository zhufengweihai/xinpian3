package uni.zf.xinpian.data

import uni.zf.xinpian.utils.generateJpUrlPrefix

object AppConst{
    const val keySecret = "secret"
    const val defaultSecret = "0sD4gjkMdbnsYp5k4K0oB5MGMggyp9UP"
    const val userAgentSuffix = ";webank/h5face;webank/1.0;netType:%s;appVersion:424;packageName:com.qihoo.jp22"
    const val networkWifi = "NETWORK_WIFI"
    const val networkMobile = "NETWORK_MOBILE"
    const val keyImgDomains = "imgDomains"
    const val defaultImgDomains = "static.ztcuc.com,img4.ztcuc.com,img.ztcuc.com,oxcljymt.top,lnijywwg.top,pxluvojc.top"
    const val version = "417"
    const val packageName = "com.qihoo.jp22"
    private val jpUrlPrefix = generateJpUrlPrefix()
    val host = "${jpUrlPrefix}.zxbwv.com"
    val imgDomainUrl = "https://${jpUrlPrefix}.zxbwv.com/api/resourceDomainConfig"
    val fenleiUrl = "https://${jpUrlPrefix}.zxbwv.com/api/term/home_fenlei"
    val initUrl = "https://${jpUrlPrefix}.zxbwv.com/api/v2/sys/init"
    val slideUrl = "https://${jpUrlPrefix}.zxbwv.com/api/slide/list?pos_id=%s"
    val tagsUrl = "https://${jpUrlPrefix}.zxbwv.com/api/customTags/list?category_id=%s"
    val dyTagURL = "https://${jpUrlPrefix}.zxbwv.com/api/dyTag/list?category_id=%s"
    val appAuthUrl = "https://${jpUrlPrefix}.zxbwv.com/api/appAuthConfig"
    val videoUrl = "https://${jpUrlPrefix}.zxbwv.com/api/video/detailv2?id=%s"
}

