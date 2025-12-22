package uni.zf.xinpian.objectbox

import android.content.Context
import io.objectbox.BoxStore
import uni.zf.xinpian.objectbox.model.MyObjectBox

object ObjectBoxManager {
    private lateinit var boxStore: BoxStore

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()
    }

    fun <T> getBox(clazz: Class<T>): io.objectbox.Box<T> {
        return boxStore.boxFor(clazz)
    }

    fun close() {
        if (::boxStore.isInitialized && !boxStore.isClosed) {
            boxStore.close()
        }
    }
}