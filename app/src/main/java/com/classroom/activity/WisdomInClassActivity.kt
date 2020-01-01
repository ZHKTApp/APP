package com.classroom.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.bright.course.App
import com.bright.course.BaseEventBusActivity
import com.bright.course.R
import com.bright.course.http.UserInfoInstance
import com.bright.course.utils.DialogUtil
import com.bright.course.utils.ToastGlobal
import com.bright.course.utils.ToastGlobal.customMsgToastShort
import com.classroom.constant.Constant
import com.classroom.mvp.model.bean.HandSupBean
import com.cxz.wanandroid.utils.SPUtil
import com.rztop.classroom.classroom.mvp.contract.HandSupContract
import com.rztop.classroom.presenter.HandSupPresenter
import com.screen.FloatingFinger
import kotlinx.android.synthetic.main.activity_classroom_inclass.*
import org.jetbrains.anko.intentFor

class WisdomInClassActivity : BaseEventBusActivity(), HandSupContract.View {

    companion object {
        fun launch(context: Context) {
            context.startActivity(context.intentFor<WisdomInClassActivity>())
        }

        fun launch(context: Context, notice: String) {
            context.startActivity(context.intentFor<WisdomInClassActivity>("notice" to notice))
        }
    }

    /**
     * 加载框
     */
    private val mDialog by lazy {
        DialogUtil.getWaitDialog(this, "请求中...")
    }

    private val mPresenter: HandSupPresenter by lazy {
        HandSupPresenter()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val notice = intent?.getStringExtra("notice")
        if (!TextUtils.isEmpty(notice)) {
            tvNotice.text = notice
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) //隐藏状态栏
        setContentView(R.layout.activity_classroom_inclass)
        //举手
//        startService(Intent(this, FloatingNotes::class.java))

        val notice = intent?.getStringExtra("notice")
        if (!TextUtils.isEmpty(notice)) {
            tvNotice.text = notice
        }

        if ("考试结束，等待评卷…" == notice) {
            tvStudentName.postDelayed({
                finish()
            }, 3000)
        }

        createChineseQRCode()

        mPresenter.attachView(this)

        //提问
//        ivAnswerStart.setOnClickListener {
//            mPresenter.handSup()//学生举手表示疑问
//        }

        //接值
        UserInfoInstance.instance.userInfo?.let {
            //学生姓名
            tvStudentName.text = "姓名：${it.profile.Name}"
            //学生学号
            tvStudentNum.text = "学号：${it.profile.Code}"
        }
    }

    override fun handSupSuccess(data: HandSupBean) {
        customMsgToastShort(App.instance, data.ID.toString())
    }

    override fun handSupFail() {
        customMsgToastShort(App.instance, getString(R.string.str_request_fail))
    }

    override fun showLoading() {
        mDialog.show()
    }

    override fun hideLoading() {
        mDialog.dismiss()
    }

    override fun showError(errorMsg: String) {
        customMsgToastShort(App.instance, errorMsg)
    }

    /**
     * 拿到（IP，端口号，wiFi名称，WiFi密码）然后生成二维码
     */
    private fun createChineseQRCode() {
        //取本地(sp)教师端-ip数据
        var qrResult by SPUtil(Constant.QR_CODE_RESULT_KEY, "127.0.0.1")

        /*
        这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
        请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github
        .com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
         */
        object : AsyncTask<Void, Void, Bitmap>() {
            override fun doInBackground(vararg params: Void): Bitmap {
                return QRCodeEncoder.syncEncodeQRCode(qrResult
                        , BGAQRCodeUtil.dp2px(this@WisdomInClassActivity, 400f)
                        , Color.parseColor("#043661"))
            }

            override fun onPostExecute(bitmap: Bitmap?) {
                if (bitmap != null) {
                    ivGenerateQr.setImageBitmap(bitmap)
                } else {
                    ToastGlobal.customMsgToastShort(App.instance, "生成二维码失败")
                }
            }
        }.execute()
    }
    var isShow = true

    private fun startService() {
        var intent = Intent(this, FloatingFinger::class.java)
        intent.putExtra("isShow", isShow)
        startService(intent)
    }
//    override fun onPause() {
//        super.onPause()
//        startService()
//        isShow = true
//
//    }
    override fun onResume() {
        super.onResume()
    if (isShow) {
        startService()
        isShow = false
    }
    }

//    override fun onStop() {
//        super.onStop()
//        if (!isShow){
//            var intent = Intent(this, FloatingFinger::class.java)
//            intent.putExtra("isShow", false)
//            startService(intent)
//        }
//    }
    override fun onDestroy() {
        super.onDestroy()
    Log.e("wisdom","ondestroy")
        var intent = Intent(this, FloatingFinger::class.java)
        intent.putExtra("isShow", false)
        startService(intent)
        stopService(Intent(this, FloatingFinger::class.java))
    }
}