package uni.zf.xinpian.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

val Context.prefs: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)
