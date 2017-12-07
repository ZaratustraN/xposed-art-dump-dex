package cn.zaratustra.dumpdex.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import cn.zaratustra.dumpdex.R
import cn.zaratustra.dumpdex.core.AppInfoCenter

/**
 * Created by zaratustra on 2017/12/5.
 */
class AppListAdapter(private var mContext: Context, private var mAppList: ArrayList<AppData>)
    : RecyclerView.Adapter<AppListItem>() {

    var mLayoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onBindViewHolder(holder: AppListItem?, position: Int) {
        holder?.let { holder.updateData(mAppList[position]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AppListItem {
        return AppListItem(mLayoutInflater.inflate(R.layout.item_app, parent, false))

    }

    override fun getItemCount(): Int {
        return mAppList.size
    }

    fun swapData(i: Int, k: Int) {
        AppInfoCenter.swapData(i, k)
        notifyItemMoved(k, i)
    }

}

class AppListItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private var mAppIcon = itemView.findViewById<View>(R.id.app_icon)
    private var mAppName = itemView.findViewById<TextView>(R.id.app_name)
    private var mAppPkgName = itemView.findViewById<TextView>(R.id.app_pkg_name)
    private var mCheckbox = itemView.findViewById<CheckBox>(R.id.check_box)

    fun updateData(appData: AppData) {
        mAppIcon.background = appData.icon
        mAppName.text = appData.appName
        mAppPkgName.text = appData.packageName
        mCheckbox.isChecked = appData.isSelected

        itemView.setOnClickListener {
            appData.isSelected = !mCheckbox.isChecked
            mCheckbox.isChecked = !mCheckbox.isChecked
            AppInfoCenter.selectedToBlock(appData, appData.isSelected)
        }
    }

}