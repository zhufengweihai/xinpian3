@file:Suppress("UNCHECKED_CAST", "USELESS_CAST", "INAPPLICABLE_JVM_NAME", "UNUSED_ANONYMOUS_PARAMETER")

package uni.UNI69B4A3A;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import io.dcloud.uniapp.framework.BasePage
import io.dcloud.uniapp.framework.onPageShow
import io.dcloud.uniapp.framework.onReady
import io.dcloud.uniapp.vue.ComponentInternalInstance
import io.dcloud.uniapp.vue.CreateVueComponent
import io.dcloud.uniapp.vue.normalizeCssStyles
import io.dcloud.uniapp.vue.normalizePropsOptions
import io.dcloud.uts.Map
import io.dcloud.uts.UTSAndroid
import io.dcloud.uts.UTSArray
import io.dcloud.uts.utsArrayOf
import io.dcloud.uts.utsMapOf
import uni.zf.xinpian.main.MainActivity

open class GenPagesIndexIndex : BasePage {
    constructor(__ins: ComponentInternalInstance, __renderer: String?) : super(__ins, __renderer) {
        onPageShow(fun() {
            val activity = UTSAndroid.getUniActivity() as AppCompatActivity
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        }, __ins);
        onReady(fun() {
            //default1()
        }
            , __ins)
    }

    @Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")
    override fun `$render`(): Any? {
        val _cache = this.`$`.renderCache;
        return null;
    }

    companion object {
        val styles: Map<String, Map<String, Map<String, Any>>>
            get() {
                return normalizeCssStyles(
                    utsArrayOf(), utsArrayOf(
                        GenApp.styles
                    )
                );
            }
        var inheritAttrs = true;
        var inject: Map<String, Map<String, Any?>> = utsMapOf();
        var emits: Map<String, Any?> = utsMapOf();
        var props = normalizePropsOptions(utsMapOf());
        var propsNeedCastKeys: UTSArray<String> = utsArrayOf();
        var components: Map<String, CreateVueComponent> = utsMapOf();
    }
}