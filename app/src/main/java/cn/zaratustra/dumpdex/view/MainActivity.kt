package cn.zaratustra.dumpdex.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import cn.zaratustra.dumpdex.R
import cn.zaratustra.dumpdex.adapter.AppData
import cn.zaratustra.dumpdex.adapter.AppListAdapter
import cn.zaratustra.dumpdex.core.AppInfoCenter
import cn.zaratustra.dumpdex.core.DumpDexNative

class MainActivity : AppCompatActivity() {

    val mRvAppList: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_app_list)
    }
    val mEtSearch: EditText by lazy {
        findViewById<EditText>(R.id.et_search)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DumpDexNative.chmodSoExecutable()
        mRvAppList.layoutManager = LinearLayoutManager(this)
        (mRvAppList.layoutManager as? LinearLayoutManager)?.orientation = LinearLayoutManager.VERTICAL
        mEtSearch.visibility = View.GONE
        AppInfoCenter.getAppList(object : AppInfoCenter.Callback {
            override fun onFinish(appList: ArrayList<AppData>) {
                mRvAppList.adapter = AppListAdapter(this@MainActivity, appList)
                mRvAppList.adapter.notifyDataSetChanged()
                mEtSearch.visibility = View.VISIBLE
            }
        })
        mEtSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && !s.isNullOrBlank()) {
                    val mapKey = ArrayList<Int>()
                    val appList = AppInfoCenter.getAppListSync()
                    (0 until appList.size step 1).filterTo(mapKey) { appList[it].appName.contains(s.toString()) }
                    for (index in 0 until mapKey.size step 1) {
                        val value = mapKey[index]
                        (mRvAppList.adapter as AppListAdapter).swapData(index, value)
                        mRvAppList.scrollToPosition(0)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

}
