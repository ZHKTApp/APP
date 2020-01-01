package com.bright.course.config

import android.content.pm.PackageInfo
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bright.course.BaseFragment
import com.bright.course.R
import kotlinx.android.synthetic.main.activity_app_list.*

/**
 * Created by kim on 2018/8/27.
 *
 */
class AppCenterListFragment : BaseFragment() {

    private val apkList = ArrayList<PackageInfo>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val pkgAppsList = activity?.getPackageManager()?.getInstalledPackages(0)!!

        pkgAppsList?.let { list ->
            list.forEach{
                var whitelist = arrayOf("eu.chainfire.supersu","com.speedsoftware.rootexplorer","com.android.dialer","cn.wps.moffice_eng")
                for (item in whitelist) {
                    if(it.packageName.toString().contains(item)){
                        apkList.add(it)
                    }
                }

            }
            val adapter = AppCenterItemAdapter()
            adapter.resultList = apkList.takeLast(4)
            val layoutManager = GridLayoutManager(context, 5)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
        }
    }

}