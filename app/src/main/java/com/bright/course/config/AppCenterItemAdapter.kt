package com.bright.course.config

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bright.course.R
import com.bright.course.wifi.OnClickWLANItemListener
import com.bumptech.glide.Glide


/**
 * Created by jinbangzhu on 25/04/2017.
 */

class AppCenterItemAdapter : RecyclerView.Adapter<AppCenterItemAdapter.ViewHolder>() {


    private var clickWLANItemListener: OnClickWLANItemListener? = null
    private lateinit var contextItemPackageName: String
    lateinit var resultList: List<PackageInfo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_center_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val info = resultList[position]
        holder.tvAppName.text = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
        Glide.with(context).load(info.applicationInfo.loadIcon(context.getPackageManager())).into(holder.ivAppIcon)
        holder.itemView.setOnClickListener {

           // val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", info.packageName, null))

                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //holder.itemView.context.startActivity(intent)
//            Log.d("context.package",context.packageManager.getLaunchIntentForPackage(info.packageName).toString())
              holder.itemView.context.startActivity(context.packageManager.getLaunchIntentForPackage(info.packageName))

//            AppInfoActivity.launch(holder.itemView.context, info)
        }
    }

    override fun getItemCount(): Int {
        return resultList.size
    }


    fun setClickWLANItemListener(clickWLANItemListener: OnClickWLANItemListener) {
        this.clickWLANItemListener = clickWLANItemListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAppName: TextView
        var ivAppIcon: ImageView

        init {
            tvAppName = itemView.findViewById(R.id.tvAppName)
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon)
        }
    }
}
