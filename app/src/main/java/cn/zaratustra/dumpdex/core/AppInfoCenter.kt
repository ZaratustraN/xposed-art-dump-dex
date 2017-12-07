package cn.zaratustra.dumpdex.core

import cn.zaratustra.dumpdex.adapter.AppData
import cn.zaratustra.dumpdex.utils.ContextFinder
import cn.zaratustra.dumpdex.utils.FileUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import java.io.File

/**
 * Created by zaratustra on 2017/12/6.
 */
object AppInfoCenter {

    private val SELECTED_APP_FILE = "/sdcard/cn.zaratustra.dumpdex/selected.list"
    private val mAllApp: HashMap<String, AppData> = HashMap()
    private val mAppList = ArrayList<AppData>()

    fun getAppList(callback: Callback) {
        if (!File(SELECTED_APP_FILE).parentFile.exists()) {
            File(SELECTED_APP_FILE).parentFile.mkdirs()
        }
        doAsync {
            mAppList.clear()
            mAllApp.clear()
            var selectedApp = getSelectedApp()
            var pkgManager = ContextFinder.getApplication().packageManager
            var packages = pkgManager.getInstalledPackages(0)
            for (it in packages) {
                var appData = AppData(it.packageName)
                appData.icon = it.applicationInfo.loadIcon(pkgManager)
                appData.appName = it.applicationInfo.loadLabel(pkgManager).toString()
                appData.isSelected = selectedApp.contains(appData.packageName)

                mAppList.add(appData)
                mAllApp[appData.packageName] = appData
            }
            uiThread {
                callback.onFinish(mAppList)
            }
        }
    }

    fun getAppListSync(): ArrayList<AppData> {
        return mAppList
    }

    interface Callback {
        fun onFinish(appList: ArrayList<AppData>)
    }

    @Synchronized
    fun selectedToBlock(appData: AppData, isSelected: Boolean) {
        var appData = mAllApp[appData.packageName]
        appData?.isSelected = isSelected
        doAsync {
            saveSelectedApp()
        }
    }


    private fun saveSelectedApp() {
        val selectedApp = mAllApp.filter { it.value.isSelected }
        val jsonArray = JSONArray()
        for (it in selectedApp.values) {
            jsonArray.put(it.packageName)
        }
        FileUtils.saveContent(jsonArray.toString(), File(SELECTED_APP_FILE), "utf-8")
    }

    @Synchronized
    fun getSelectedApp(): ArrayList<String> {
        val result = ArrayList<String>()
        try {
            val jsonArrayStr = FileUtils.readFile(File(SELECTED_APP_FILE), "utf-8")
            val jsonArray = JSONArray(jsonArrayStr)
            (0 until jsonArray.length() step 1).mapTo(result) { it -> jsonArray[it].toString() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun swapData(i: Int, k: Int) {
        var appData = mAppList.removeAt(k)
        mAppList.add(i, appData)
    }


}