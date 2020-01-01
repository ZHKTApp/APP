package com.bright.course.config

import android.app.LauncherActivity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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



class AppManageListFragment : BaseFragment() {

    private val apkList = ArrayList<PackageInfo>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pkgAppsList = activity?.getPackageManager()?.getInstalledPackages(0)
        val adapter = AppItemAdapter(listener = {
//            context?.let { context -> AppInfoActivity.launch(context, it) }
        })


        pkgAppsList?.let { list ->



            list.forEach{
                if(it.packageName.toString().contains("com.bright")){
                    apkList.add(it)
                }
            }
            adapter.resultList =  apkList
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter
        }


    }
}