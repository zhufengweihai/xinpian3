@file:Suppress("UNCHECKED_CAST", "USELESS_CAST", "INAPPLICABLE_JVM_NAME", "UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING", "UNNECESSARY_NOT_NULL_ASSERTION")
package uni.UNI69B4A3A;
import io.dcloud.uniapp.*
import io.dcloud.uniapp.extapi.*
import io.dcloud.uniapp.framework.*
import io.dcloud.uniapp.runtime.*
import io.dcloud.uniapp.vue.*
import io.dcloud.uniapp.vue.shared.*
import io.dcloud.unicloud.*
import io.dcloud.uts.*
import io.dcloud.uts.Map
import io.dcloud.uniapp.extapi.exit as uni_exit
import io.dcloud.uniapp.extapi.getSystemInfoSync as uni_getSystemInfoSync
import io.dcloud.uniapp.extapi.openDialogPage as uni_openDialogPage
import io.dcloud.uniapp.extapi.removeStorageSync as uni_removeStorageSync
import io.dcloud.uniapp.extapi.setStorageSync as uni_setStorageSync
import io.dcloud.uniapp.extapi.showToast as uni_showToast
import uts.sdk.modules.utsOpenSchema.openSchema as utsOpenSchema

val runBlock1 = run {
    __uniConfig.getAppStyles = fun(): Map<String, Map<String, Map<String, Any>>> {
        return GenApp.styles
    }
}
var firstBackTime: Number = 0
open class GenApp : BaseApp {
    constructor(__ins: ComponentInternalInstance) : super(__ins) {
        onLaunch(fun(_: OnLaunchOptions) {
            console.log("App Launch")
        }
        , __ins)
        onAppShow(fun(_: OnShowOptions) {
            console.log("App Show")
        }
        , __ins)
        onAppHide(fun() {
            console.log("App Hide")
        }
        , __ins)
        onLastPageBackPress(fun() {
            console.log("App LastPageBackPress")
            if (firstBackTime == 0) {
                uni_showToast(ShowToastOptions(title = "再按一次退出应用", position = "bottom"))
                firstBackTime = Date.now()
                setTimeout(fun(){
                    firstBackTime = 0
                }, 2000)
            } else if (Date.now() - firstBackTime < 2000) {
                firstBackTime = Date.now()
                uni_exit(null)
            }
        }
        , __ins)
        onExit(fun() {
            console.log("App Exit")
        }
        , __ins)
    }
    companion object {
        val styles: Map<String, Map<String, Map<String, Any>>> by lazy {
            normalizeCssStyles(utsArrayOf(
                styles0
            ))
        }
        val styles0: Map<String, Map<String, Map<String, Any>>>
            get() {
                return utsMapOf("uni-row" to padStyleMapOf(utsMapOf("flexDirection" to "row")), "uni-column" to padStyleMapOf(utsMapOf("flexDirection" to "column")))
            }
    }
}
val GenAppClass = CreateVueAppComponent(GenApp::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "app", name = "", inheritAttrs = true, inject = Map(), props = Map(), propsNeedCastKeys = utsArrayOf(), emits = Map(), components = Map(), styles = GenApp.styles)
}
, fun(instance): GenApp {
    return GenApp(instance)
}
)
open class StoreListItem (
    @JsonNotNull
    open var enable: Boolean = false,
    @JsonNotNull
    open var id: String,
    @JsonNotNull
    open var name: String,
    @JsonNotNull
    open var scheme: String,
    @JsonNotNull
    open var priority: Number,
) : UTSReactiveObject() {
    override fun __v_create(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): UTSReactiveObject {
        return StoreListItemReactiveObject(this, __v_isReadonly, __v_isShallow, __v_skip)
    }
}
class StoreListItemReactiveObject : StoreListItem, IUTSReactive<StoreListItem> {
    override var __v_raw: StoreListItem
    override var __v_isReadonly: Boolean
    override var __v_isShallow: Boolean
    override var __v_skip: Boolean
    constructor(__v_raw: StoreListItem, __v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean) : super(enable = __v_raw.enable, id = __v_raw.id, name = __v_raw.name, scheme = __v_raw.scheme, priority = __v_raw.priority) {
        this.__v_raw = __v_raw
        this.__v_isReadonly = __v_isReadonly
        this.__v_isShallow = __v_isShallow
        this.__v_skip = __v_skip
    }
    override fun __v_clone(__v_isReadonly: Boolean, __v_isShallow: Boolean, __v_skip: Boolean): StoreListItemReactiveObject {
        return StoreListItemReactiveObject(this.__v_raw, __v_isReadonly, __v_isShallow, __v_skip)
    }
    override var enable: Boolean
        get() {
            return trackReactiveGet(__v_raw, "enable", __v_raw.enable, this.__v_isReadonly, this.__v_isShallow)
        }
        set(value) {
            if (!this.__v_canSet("enable")) {
                return
            }
            val oldValue = __v_raw.enable
            __v_raw.enable = value
            triggerReactiveSet(__v_raw, "enable", oldValue, value)
        }
    override var id: String
        get() {
            return trackReactiveGet(__v_raw, "id", __v_raw.id, this.__v_isReadonly, this.__v_isShallow)
        }
        set(value) {
            if (!this.__v_canSet("id")) {
                return
            }
            val oldValue = __v_raw.id
            __v_raw.id = value
            triggerReactiveSet(__v_raw, "id", oldValue, value)
        }
    override var name: String
        get() {
            return trackReactiveGet(__v_raw, "name", __v_raw.name, this.__v_isReadonly, this.__v_isShallow)
        }
        set(value) {
            if (!this.__v_canSet("name")) {
                return
            }
            val oldValue = __v_raw.name
            __v_raw.name = value
            triggerReactiveSet(__v_raw, "name", oldValue, value)
        }
    override var scheme: String
        get() {
            return trackReactiveGet(__v_raw, "scheme", __v_raw.scheme, this.__v_isReadonly, this.__v_isShallow)
        }
        set(value) {
            if (!this.__v_canSet("scheme")) {
                return
            }
            val oldValue = __v_raw.scheme
            __v_raw.scheme = value
            triggerReactiveSet(__v_raw, "scheme", oldValue, value)
        }
    override var priority: Number
        get() {
            return trackReactiveGet(__v_raw, "priority", __v_raw.priority, this.__v_isReadonly, this.__v_isShallow)
        }
        set(value) {
            if (!this.__v_canSet("priority")) {
                return
            }
            val oldValue = __v_raw.priority
            __v_raw.priority = value
            triggerReactiveSet(__v_raw, "priority", oldValue, value)
        }
}
open class UniUpgradeCenterResult (
    @JsonNotNull
    open var _id: String,
    @JsonNotNull
    open var appid: String,
    @JsonNotNull
    open var name: String,
    @JsonNotNull
    open var title: String,
    @JsonNotNull
    open var contents: String,
    @JsonNotNull
    open var url: String,
    @JsonNotNull
    open var platform: UTSArray<String>,
    @JsonNotNull
    open var version: String,
    @JsonNotNull
    open var uni_platform: String,
    @JsonNotNull
    open var stable_publish: Boolean = false,
    @JsonNotNull
    open var is_mandatory: Boolean = false,
    open var is_silently: Boolean? = null,
    @JsonNotNull
    open var create_env: String,
    @JsonNotNull
    open var create_date: Number,
    @JsonNotNull
    open var message: String,
    @JsonNotNull
    open var code: Number,
    @JsonNotNull
    open var type: String,
    open var store_list: UTSArray<StoreListItem>? = null,
    open var min_uni_version: String? = null,
) : UTSObject()
fun `default`(): UTSPromise<UniUpgradeCenterResult> {
    return UTSPromise<UniUpgradeCenterResult>(fun(resolve, reject){
        val systemInfo = uni_getSystemInfoSync()
        val appId = systemInfo.appId
        val appVersion = systemInfo.appVersion
        if (UTSAndroid.`typeof`(appId) === "string" && UTSAndroid.`typeof`(appVersion) === "string" && appId.length > 0 && appVersion.length > 0) {
            var data: UTSJSONObject = UTSJSONObject(Map<String, Any?>(utsArrayOf(
                utsArrayOf(
                    "action",
                    "checkVersion"
                ),
                utsArrayOf(
                    "appid",
                    appId
                ),
                utsArrayOf(
                    "appVersion",
                    appVersion
                ),
                utsArrayOf(
                    "is_uniapp_x",
                    true
                ),
                utsArrayOf(
                    "wgtVersion",
                    "0.0.0.0.0.1"
                )
            )))
            try {
                uniCloud.callFunction(UniCloudCallFunctionOptions(name = "uni-upgrade-center", data = data)).then(fun(res){
                    val code = res.result["code"]
                    val codeIsNumber = utsArrayOf(
                        "Int",
                        "Long",
                        "number"
                    ).includes(UTSAndroid.`typeof`(code))
                    if (codeIsNumber) {
                        if ((code as Number) == 0) {
                            reject(object : UTSJSONObject() {
                                var code = res.result["code"]
                                var message = res.result["message"]
                            })
                        } else if ((code as Number) < 0) {
                            reject(object : UTSJSONObject() {
                                var code = res.result["code"]
                                var message = res.result["message"]
                            })
                        } else {
                            val result = JSON.parse<UniUpgradeCenterResult>(JSON.stringify(res.result)) as UniUpgradeCenterResult
                            resolve(result)
                        }
                    }
                }).`catch`<Unit>(fun(err: Any?){
                    val error = err as UniCloudError
                    if (error.errMsg == "未匹配到云函数[uni-upgrade-center]") {
                        error.errMsg = "【uni-upgrade-center-app】未配置uni-upgrade-center，无法升级。参考: https://uniapp.dcloud.net.cn/uniCloud/upgrade-center.html"
                    }
                    reject(error.errMsg)
                })
            } catch (e: Throwable) {
                reject(e.message)
            }
        } else {
            reject("invalid appid or appVersion")
        }
    }
    )
}
val platform_iOS: String = "iOS"
val platform_Android: String = "Android"
val platform_Harmony: String = "Harmony"
val PACKAGE_INFO_KEY = "__package_info__"
fun default1(): UTSPromise<UniUpgradeCenterResult> {
    return UTSPromise<UniUpgradeCenterResult>(fun(resolve, reject){
        `default`().then(fun(uniUpgradeCenterResult): UTSPromise<Unit> {
            return wrapUTSPromise(suspend w@{
                    val code = uniUpgradeCenterResult.code
                    val message = uniUpgradeCenterResult.message
                    val url = uniUpgradeCenterResult.url
                    if (code > 0) {
//                        if (UTSRegExp("^cloud:\\/\\/", "").test(url)) {
//                            val tcbRes = await(uniCloud.getTempFileURL(UniCloudGetTempFileURLOptions(fileList = utsArrayOf(
//                                url
//                            ))))
//                            if (UTSAndroid.`typeof`(tcbRes.fileList[0].tempFileURL) !== "undefined") {
//                                uniUpgradeCenterResult.url = tcbRes.fileList[0].tempFileURL
//                            }
//                        }
                        uni_setStorageSync(PACKAGE_INFO_KEY, uniUpgradeCenterResult)
                        uni_openDialogPage(OpenDialogPageOptions(url = "/uni_modules/uni-upgrade-center-app/pages/uni-app-x/upgrade-popup?local_storage_key=" + PACKAGE_INFO_KEY, disableEscBack = true, fail = fun(err){
                            console.error("更新弹框跳转失败", err)
                            uni_removeStorageSync(PACKAGE_INFO_KEY)
                        }))
                        return@w resolve(uniUpgradeCenterResult)
                    } else if (code < 0) {
                        console.error(message)
                        return@w reject(uniUpgradeCenterResult)
                    }
                    return@w resolve(uniUpgradeCenterResult)
            })
        }
        ).`catch`(fun(err){
            reject(err)
        }
        )
    }
    )
}
val GenPagesIndexIndexClass = CreateVueComponent(GenPagesIndexIndex::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenPagesIndexIndex.inheritAttrs, inject = GenPagesIndexIndex.inject, props = GenPagesIndexIndex.props, propsNeedCastKeys = GenPagesIndexIndex.propsNeedCastKeys, emits = GenPagesIndexIndex.emits, components = GenPagesIndexIndex.components, styles = GenPagesIndexIndex.styles)
}
, fun(instance, renderer): GenPagesIndexIndex {
    return GenPagesIndexIndex(instance, renderer)
}
)
val requiredKey = utsArrayOf(
    "version",
    "url",
    "type"
)
var downloadTask: DownloadTask? = null
var openSchemePromise: UTSPromise<Boolean>? = null
val openSchema1 = fun(url: String): UTSPromise<Boolean> {
    return UTSPromise<Boolean>(fun(resolve, reject){
        try {
            utsOpenSchema(url)
            resolve(true)
        }
         catch (e: Throwable) {
            reject(false)
        }
    }
    )
}
val GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopupClass = CreateVueComponent(
    GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup::class.java, fun(): VueComponentOptions {
    return VueComponentOptions(type = "page", name = "", inheritAttrs = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.inheritAttrs, inject = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.inject, props = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.props, propsNeedCastKeys = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.propsNeedCastKeys, emits = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.emits, components = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.components, styles = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup.styles)
}
, fun(instance, renderer): GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup {
    return GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopup(instance, renderer)
}
)
fun createApp(): UTSJSONObject {
    val app = createSSRApp(GenAppClass)
    return UTSJSONObject(Map<String, Any?>(utsArrayOf(
        utsArrayOf(
            "app",
            app
        )
    )))
}
fun main(app: IApp) {
    definePageRoutes()
    defineAppConfig()
    (createApp()["app"] as VueApp).mount(app, GenUniApp())
}
open class UniAppConfig : io.dcloud.uniapp.appframe.AppConfig {
    override var name: String = "xinpian";
    override var appid: String = "__UNI__69B4A3A";
    override var versionName: String = "2.1.0";
    override var versionCode: String = "3";
    override var uniCompilerVersion: String = "4.82"
    constructor() : super() {}
}
fun definePageRoutes() {
    __uniRoutes.push(UniPageRoute(path = "pages/index/index", component = GenPagesIndexIndexClass, meta = UniPageMeta(isQuit = true), style = utsMapOf("navigationBarTitleText" to "uni-app x")))
    __uniRoutes.push(UniPageRoute(path = "uni_modules/uni-upgrade-center-app/pages/uni-app-x/upgrade-popup", component = GenUniModulesUniUpgradeCenterAppPagesUniAppXUpgradePopupClass, meta = UniPageMeta(isQuit = false), style = utsMapOf("navigationBarTitleText" to "")))
}
val __uniLaunchPage: Map<String, Any?> = utsMapOf("url" to "pages/index/index", "style" to utsMapOf("navigationBarTitleText" to "uni-app x"))
fun defineAppConfig() {
    __uniConfig.entryPagePath = "/pages/index/index"
    __uniConfig.globalStyle = utsMapOf("navigationBarTextStyle" to "black", "navigationBarTitleText" to "uni-app x", "navigationBarBackgroundColor" to "#F8F8F8", "backgroundColor" to "#F8F8F8")
    __uniConfig.getTabBarConfig = fun(): Map<String, Any>? {
        return null
    }
    __uniConfig.tabBar = __uniConfig.getTabBarConfig()
    __uniConfig.conditionUrl = ""
    __uniConfig.uniIdRouter = utsMapOf()
    __uniConfig.ready = true
}
open class UniCloudConfig : io.dcloud.unicloud.InternalUniCloudConfig {
    override var isDev: Boolean = false
    override var spaceList: String = "[{\"provider\":\"alipay\",\"spaceName\":\"xinpian\",\"spaceId\":\"env-00jx4sldke3h\",\"spaceAppId\":\"2021004125602779\",\"accessKey\":\"s2g2qv4NV6rDemp4\",\"secretKey\":\"lq1e0JqT4ohsT8jO\"}]"
    override var debuggerInfo: String? = null
    override var secureNetworkEnable: Boolean = false
    override var secureNetworkConfig: String? = ""
    constructor() : super() {}
}
open class GenUniApp : UniAppImpl() {
    open val vm: GenApp?
        get() {
            return getAppVm() as GenApp?
        }
    open val `$vm`: GenApp?
        get() {
            return getAppVm() as GenApp?
        }
}
fun getApp(): GenUniApp {
    return getUniApp() as GenUniApp
}
