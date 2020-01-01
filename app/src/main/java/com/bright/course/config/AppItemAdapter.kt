package com.bright.course.config

import android.content.Intent
import android.content.pm.IPackageDataObserver
import android.content.pm.PackageInfo
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bright.course.R
import com.bright.course.utils.HumanReadableUnit
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.app_item.view.*
import java.io.FileInputStream
import java.lang.reflect.InvocationTargetException


/**
 * Created by jinbangzhu on 25/04/2017.
 */

class AppItemAdapter(private val listener: (PackageInfo) -> Unit) : RecyclerView.Adapter<AppItemAdapter.ViewHolder>() {

    lateinit var resultList: List<PackageInfo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val info = resultList[position]
        holder.bind(info, position, listener)
    }

    override fun getItemCount(): Int {
        return resultList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(info: PackageInfo, position: Int, listener: (PackageInfo) -> Unit) = with(itemView) {
            if (position == 0) btnAllUpdate.visibility = View.VISIBLE else btnAllUpdate.visibility = View.GONE

            tvAppName.text = info.applicationInfo.loadLabel(context.packageManager).toString();
            tvAppVersion.text = info.versionName

            updateApp(position)// 更新

            val sourceSize = FileInputStream(info.applicationInfo.sourceDir).channel.size();
            val dataSize = try {
                FileInputStream(info.applicationInfo.dataDir).getChannel().size();
            } catch (e: Exception) {
                0L
            }
            val publicSourceSize = FileInputStream(info.applicationInfo.publicSourceDir).channel.size();
            tvAppSize.text = HumanReadableUnit.ByteWithUnitSuffixes(sourceSize + dataSize + publicSourceSize)

            Glide.with(context).load(info.applicationInfo.loadIcon(context.packageManager)).into(ivAppIcon)

            setOnClickListener { listener(info) }


            btnAppClean.setOnClickListener {
                clearCache()

            }
            btnUnInstall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:${info.packageName}")
                context.startActivity(intent)
            }
        }

        /**
         * 更新按钮
         */
        private fun View.updateApp(position: Int) {
            when (position) {
                0 -> {
                    cirProgress.setProgress(50)
                    btnUpgrade?.text = "50%"
                    btnUpgrade?.letterSpacing = 0f
                }
                1 -> {
                    cirProgress?.setProgress(100)
                    btnUpgrade?.text = "更新"
                    btnUpgrade?.letterSpacing = 0.4f
                }
                else -> {
                    cirProgress?.setProgress(0)
                    btnUpgrade?.text = "更新"
                    btnUpgrade?.letterSpacing = 0.4f
                }
            }
        }

        private fun View.clearCache() {
            val mClearCacheObserver = CachePackageDataObserver()

            val mPM = context.packageManager

            val localLong = java.lang.Long.valueOf(Long.MAX_VALUE)

            val classes = arrayOf(java.lang.Long.TYPE, IPackageDataObserver::class.java)

            try {
                val localMethod = mPM.javaClass.getMethod("freeStorageAndNotify", *classes)
                /*
                     * Start of inner try-catch block
                     */
                try {
                    localMethod.invoke(mPM, localLong, mClearCacheObserver)
                } catch (e: IllegalArgumentException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }

                /*
                     * End of inner try-catch block
                     */
            } catch (e1: NoSuchMethodException) {
                // TODO Auto-generated catch block
                e1.printStackTrace()
            }
        }
    }


    private class CachePackageDataObserver : IPackageDataObserver.Stub() {
        override fun onRemoveCompleted(packageName: String, succeeded: Boolean) {

        }//End of onRemoveCompleted() method
    }//End of CachePackageDataObserver instance inner class
}
