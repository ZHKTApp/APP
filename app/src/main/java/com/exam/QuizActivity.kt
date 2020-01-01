package com.exam

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import com.bright.course.BaseEventBusActivity
import com.bright.course.R
import com.bright.course.http.APICallback
import com.bright.course.http.APIService
import com.bright.course.http.UserInfoInstance
import com.bright.course.http.response.ResponseDataT
import com.bright.course.http.response.ResponseQuiz
import com.bright.course.http.response.ResponseUploadImage
import com.bright.course.utils.StreamUtils
import com.bright.course.utils.ToastGlobal
import com.bright.course.utils.ViewHelper
import com.utils.ImageUtils
import kotlinx.android.synthetic.main.activity_quiz.*
import me.panavtec.drawableview.DrawableView
import me.panavtec.drawableview.DrawableViewConfig
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.uiThread
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.net.URL


/**
 * Created by kim on 2018/9/12.
 *
 */
class QuizActivity : BaseEventBusActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    var isShowSubjective: Boolean = false
    lateinit var subjectiveFragment: ExamAnswerSubjectiveFragment
    lateinit var paintConfig: DrawableViewConfig
    var questionId: String = ""
    val ANSWER_TYPE_OPTIONS = "1"
    val ANSWER_TYPE_TRUR_FALSE = "2"
    val ANSWER_TYPE_TRUR_SUBJECTIVE = "3"


    companion object {
        fun launch(context: Context, questionId: String) {
            context.startActivity(context.intentFor<QuizActivity>("questionId" to questionId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionId = intent.getStringExtra("questionId")
        setContentView(R.layout.activity_quiz)

        UserInfoInstance.instance.userInfo?.let {
            tvStudentInfo.text = "姓名：${it.profile.Name} 学号：${it.profile.Code}"
        }

        initListener()

        loadQuiz()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            questionId = it.getStringExtra("questionId")
            loadQuiz()
        }

    }

    //修改
    var checkBoxArr: Array<CheckBox>? = null

    private fun initListener() {
        btnQuizSubjectiveAnswer.setOnClickListener(this)
        btnQuizSubjectiveDraw.setOnClickListener(this)



        btnHandPaintCancel.setOnClickListener(this)
        btnHandPaintSubmit.setOnClickListener(this)
        btnPaintErase.setOnClickListener(this)
        btnPaintUndo.setOnClickListener(this)
        btnPaintRedo.setOnClickListener(this)
        btnPaintEmpty.setOnClickListener(this)

        btnPaintColorWhite.setOnClickListener(this)
        btnPaintColorBlack.setOnClickListener(this)
        btnPaintColorGreen.setOnClickListener(this)
        btnPaintColorRed.setOnClickListener(this)
        btnPaintColorYellow.setOnClickListener(this)
        btnPaintColorBlue.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)

        checkBoxArr = arrayOf(cbxRight, cbxWrong)

        cbxWrong.setOnClickListener(this)
        cbxRight.setOnClickListener(this)

        cbxRight.setOnCheckedChangeListener(this)
        cbxWrong.setOnCheckedChangeListener(this)

        cbxAnswerA.setOnClickListener(this)
        cbxAnswerB.setOnClickListener(this)
        cbxAnswerC.setOnClickListener(this)
        cbxAnswerD.setOnClickListener(this)
        cbxAnswerE.setOnClickListener(this)
        cbxAnswerF.setOnClickListener(this)


        paintConfig = DrawableViewConfig()
        paintConfig.strokeColor = resources.getColor(android.R.color.black)
        paintConfig.isShowCanvasBounds = true
        paintConfig.strokeWidth = DrawableViewConfig.DEFAULT_LINE_SIZE
        paintConfig.minZoom = 1.0f
        paintConfig.maxZoom = 2.0f
        paintConfig.canvasHeight = resources.displayMetrics.heightPixels
        paintConfig.canvasWidth = resources.displayMetrics.widthPixels
        fingerView.setConfig(paintConfig)
    }

    private fun loadQuiz() {
        showLoadingDialog("正在加载题目，请稍等…")
        val call = APIService.create().getQuizQuestion(questionId)
        call.enqueue(object : APICallback<ResponseDataT<ResponseQuiz>>() {
            override fun onSuccess(response: ResponseDataT<ResponseQuiz>?) {
                downloadFile(response?.data?.Url)
                enableViews(llBottom)
                setOptionButtonUnChecked()
                setRightOrWrongUnChecked()
                if (::subjectiveFragment.isInitialized) {
                    subjectiveFragment.enableSubmitButton()
                }
//                Glide.with(this@QuizActivity).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542528844371&di=09bef1b75bb4eb4bec1d1d639a59b8cc&imgtype=0&src=http%3A%2F%2Fimages6.fanpop.com%2Fimage%2Fphotos%2F37000000%2Fmarvel-hero-marvel-comics-37040128-1600-900.jpg").into(ivSubject)

            }

            override fun onFinish(msg:String) {
//                dismissLoadingDialog()
            }

        })

        callList.add(call)
    }


    fun downloadFile(url: String?) {
        url?.let {
            showLoadingDialog("正在下载提问内容，请稍等")
            val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
            doAsync {
                var file: File? = null
                try {
                    val inputStream = BufferedInputStream(URL(url).openStream(), StreamUtils.IO_BUFFER_SIZE)
                    val downloadPath = File(Environment.getExternalStorageDirectory(), "bc_downloads")
                    if (!downloadPath.exists()) downloadPath.mkdirs()
//                val inputStream = BufferedInputStream(URL("http://foooooot.com/media/upload/selected_route/%E6%A1%83%E8%8A%B1%E5%B2%9B.jpg").openStream(), IO_BUFFER_SIZE)
                    file = File(downloadPath, fileName)
                    val outputStream = FileOutputStream(file, false)
                    val out = BufferedOutputStream(outputStream, StreamUtils.IO_BUFFER_SIZE)
                    StreamUtils.copy(inputStream, out)
                    out.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                uiThread {
                    dismissLoadingDialog()
                    if (file != null) {
//                        Glide.with(this@QuizActivity).load(file).into(ivSubject)
                        doAsync {
                            val bitmapHeight = ImageUtils.getBitmapHeight(file.absolutePath)
                            var height = resources.displayMetrics.heightPixels
                            val width = resources.displayMetrics.widthPixels

                            //xiugai
//                            height = if (bitmapHeight > height) bitmapHeight else height

                            paintConfig.canvasHeight = (height*0.85).toInt()
                            fingerView.setConfig(paintConfig)

                            height = (height*1.2).toInt()
                            val bitmap = ImageUtils.decodeFile(file.absolutePath, width, height)
                            ivSubject.setClearModel(true)
                            ivSubject.setBitmap(bitmap)
                            fingerView.setBitmap(bitmap)
                            ivSubject.setJustBitmapModel(true)
                            ivSubject.setConfig(paintConfig)
                            uiThread {
                                dismissLoadingDialog()
                                ivSubject.invalidate()
                            }
                        }
                    } else {
                        ToastGlobal.showToast("下载失败")
                    }
                }
            }
        }
    }

    var eraseModel = false
    override fun onClick(v: View?) {
        when (v) {
            btnQuizSubjectiveAnswer -> showSubjectiveView()
            btnQuizSubjectiveDraw -> showHandDrawView()
            btnHandPaintCancel -> hideHandDrawView()

            btnPaintColorWhite -> paintConfig.strokeColor = Color.WHITE
            btnPaintColorBlack -> paintConfig.strokeColor = Color.BLACK
            btnPaintColorGreen -> paintConfig.strokeColor = Color.GREEN
            btnPaintColorRed -> paintConfig.strokeColor = Color.RED
            btnPaintColorYellow -> paintConfig.strokeColor = Color.YELLOW
            btnPaintColorBlue -> paintConfig.strokeColor = Color.BLUE

            btnPaintEmpty -> fingerView.clear()
            btnPaintUndo -> fingerView.undo()
            btnPaintRedo -> fingerView.redo()
            btnPaintErase -> {
                eraseModel = !eraseModel
                fingerView.setClearModel(eraseModel)
                btnPaintErase.setBackgroundResource(if (eraseModel) R.drawable.shape_erase_selected else R.drawable.shape_erase_un_selected)
            }
            cbxAnswerA, cbxAnswerB, cbxAnswerC, cbxAnswerD, cbxAnswerD, cbxAnswerE, cbxAnswerF -> {
                setRightOrWrongUnChecked()
            }
            cbxWrong, cbxRight -> {
                setOptionButtonUnChecked()
            }

            btnHandPaintSubmit -> {
                if (fingerView.isEmpty) {
                    ToastGlobal.showToast("请先作答~")
                    return
                }
                setRightOrWrongUnChecked()
                setOptionButtonUnChecked()
                captureViewAndUpload()
            }
            btnSubmit -> {
                if (cbxWrong.isChecked || cbxRight.isChecked) {
                    postAnswer(ANSWER_TYPE_TRUR_FALSE, if (cbxRight.isChecked) "true" else "false")
                } else if (isAnyOneOptionChecked()) {
                    postAnswer(ANSWER_TYPE_OPTIONS, getCheckedOption())
                } else {
                    ToastGlobal.showToast("请先作答~")
                }
                //修改
//                setRightOrWrongUnChecked()
//                setOptionButtonUnChecked()
            }
        }
        fingerView.setConfig(paintConfig)
     }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked)
            for (checkBos in checkBoxArr!!) {
                if (checkBos.text.toString().equals(buttonView?.text.toString())) checkBos.isChecked = true else checkBos.isChecked = false
            }
    }

    private fun isAnyOneOptionChecked(): Boolean {
        for (i in 0 until llOptions.childCount) {
            val view: CheckBox = llOptions.getChildAt(i) as CheckBox
            if (view.isChecked) return true
        }
        return false
    }

    private fun getCheckedOption(): String {
        val stringBuilder: StringBuilder = StringBuilder()
        for (i in 0 until llOptions.childCount) {
            val view: CheckBox = llOptions.getChildAt(i) as CheckBox
            if (view.isChecked) stringBuilder.append(view.text)
        }
        return stringBuilder.toString()
    }

    private fun setRightOrWrongUnChecked() {
        cbxRight.isChecked = false
        cbxWrong.isChecked = false
    }

    private fun captureViewAndUpload() {
        showLoadingDialog("正在处理中，请稍等")

        doAsync {
            val tempImageFile = File(cacheDir, "temp.jpg")
            ViewHelper.captureViewWithDrawingCache(frameAnswerArea, tempImageFile.absolutePath)
            Log.e("upload","upload : "+ tempImageFile)
            uiThread {
                uploadImage(tempImageFile)
            }
      //      finish();
       //     startActivity(getIntent());
        }
    }


    private fun uploadImage(tempImageFile: File) {
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), tempImageFile)

        val body = MultipartBody.Part.createFormData("file", tempImageFile.getName(), requestBody)

        val call = APIService.create().uploadImage(body)
        call.enqueue(object : APICallback<ResponseDataT<ResponseUploadImage>>() {
            override fun onSuccess(response: ResponseDataT<ResponseUploadImage>?) {
                if (!TextUtils.isEmpty(response?.data?.url)) {
                    postAnswer(ANSWER_TYPE_TRUR_SUBJECTIVE, response?.data?.url!!)
                } else {
                    ToastGlobal.showToast("上传图片失败")
                }
            }

            override fun onFinish(msg:String) {
                dismissLoadingDialog()
            }
        })
        callList.add(call)
    }

    private fun postAnswer(type: String, answer: String) {
        showLoadingDialog("正在上传答案，请稍等")
        val answerCall = APIService.create().postQuestion(questionId, type, answer)
        Log.e("answer","answer : " + answer)
        answerCall.enqueue(object : APICallback<ResponseDataT<Any?>>() {

            override fun onSuccess(response: ResponseDataT<Any?>?) {
                ToastGlobal.showToast("成功回答问题")
                //修改

//                btnHandPaintCancel.performClick()
                if (::subjectiveFragment.isInitialized) {
                    subjectiveFragment.disableSubmitButton()
                }
                disableViews(llBottom)
//                WisdomInClassActivity.launch(this@QuizActivity, "教师讲评中")
//                finish()
                //修改
                btnHandPaintCancel.performClick()
            }

            override fun onFinish(msg:String) {
                dismissLoadingDialog()
            }


        })

        callList.add(answerCall)
    }

    private fun setOptionButtonUnChecked() {
        for (i in 0 until llOptions.childCount) {
            val view: CheckBox = llOptions.getChildAt(i) as CheckBox
            view.isChecked = false

        }
    }

    fun showHandDrawView() {
        setRightOrWrongUnChecked()
        setOptionButtonUnChecked()
        fingerView.visibility = View.VISIBLE
        ivSubject.visibility = View.GONE
        llHandDrawOperation.visibility = View.VISIBLE
        llBottom.visibility = View.GONE
        fingerView.clear()
    }

    fun hideHandDrawView() {
        fingerView.visibility = View.GONE
        ivSubject.visibility = View.VISIBLE
        llHandDrawOperation.visibility = View.GONE
        llBottom.visibility = View.VISIBLE
    }

    /**
     * 显示主观题
     */
    private fun showSubjectiveView() {
        isShowSubjective = true
        subjectiveContainer.visibility = View.VISIBLE
        setRightOrWrongUnChecked()
        setOptionButtonUnChecked()
//        if (supportFragmentManager.findFragmentByTag("subjective") == null) {
        subjectiveFragment = ExamAnswerSubjectiveFragment.instance()
        supportFragmentManager.beginTransaction().replace(R.id.subjectiveContainer, subjectiveFragment, "subjective").commit()
//        }
    }

    /**
     * 隐藏主观题
     */
    public fun hideSubjectiveView(fragment: ExamAnswerSubjectiveFragment) {
        isShowSubjective = false
        subjectiveContainer.visibility = View.GONE

        supportFragmentManager.findFragmentById(R.id.subjectiveContainer).view?.findViewById<DrawableView>(R.id.fingerView)?.clear()
    }

    public fun postAnswerFromSubjectiveView(filePath: String) {
        postAnswer(ANSWER_TYPE_TRUR_SUBJECTIVE, filePath)
    }


    private fun enableViews(view: ViewGroup) {
        for (position in 0 until view.childCount) {
            val currentView = view.getChildAt(position)
            if (currentView is ViewGroup) {
                enableViews(currentView)
            } else {
                currentView.isEnabled = true
            }
        }
    }

    private fun disableViews(view: ViewGroup) {
        for (position in 0 until view.childCount) {
            val currentView = view.getChildAt(position)
            if (currentView is ViewGroup) {
                disableViews(currentView)
            } else {
                currentView.isEnabled = false
            }
        }
    }

}