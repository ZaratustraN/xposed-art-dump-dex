package cn.zaratustra.dumpdex.adapter

import android.graphics.drawable.Drawable

/**
 * Created by zaratustra on 2017/12/6.
 */
data class AppData(var packageName: String) {
    var appName: String = ""
    var icon: Drawable? = null
    var isSelected: Boolean = false
}